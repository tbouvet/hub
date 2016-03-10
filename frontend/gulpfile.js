// jshint ignore: start
const gulp = require('gulp');
const typescript = require('gulp-typescript');
const bower = require('gulp-bower');
const less = require('gulp-less');
const del = require('del');
const tscConfig = require('./tsconfig.json');
const sourcemaps = require('gulp-sourcemaps');
const htmlreplace = require('gulp-html-replace');
const fs = require('fs');
var Server = require('karma').Server;
require('gulp-grunt')(gulp);

gulp.task('clean', function () {
    return del('dist/**/*');
});

gulp.task('bower', function() {
    return bower();
});

gulp.task('less-compile', function () {
    return gulp.src('hub/style/**/*.less')
    .pipe(less())
    .pipe(gulp.dest('hub/style'));
});

gulp.task('ts-compile', function () {
    return gulp
        .src(tscConfig.files)
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('hub'));
});

gulp.task('copy', ['bower', 'clean', 'ts-compile', 'less-compile'], function () {
    return gulp.src([
            'bower_components/**/*',
            'hub/**/*',
            '!hub/**/*.ts',
            //'!hub/**/*.js',
            '!hub/**/*.less',
            'w20.app.json'],
        { base : './' })
        .pipe(gulp.dest('dist'));
});

gulp.task('test', ['bower', 'ts-compile'], function (done) {
    new Server({
        configFile: __dirname + '/karma.conf.js',
        singleRun: true
    }, done).start();
});

gulp.task('optimize', ['ts-compile', 'copy'], function () {
    return gulp.start('grunt-default');
});

gulp.task('replace-index', ['copy'], function() {
    gulp.src('index.html')
        .pipe(htmlreplace({
            replace: {
                src: 'hub.min.js',
                tpl: '<script src="%s"></script>'
            }
        }))
        .pipe(gulp.dest('dist/'));
});

gulp.task('replace-manifest', ['copy'], function () {
    function buildKey (key) {
        var arr = key.split('/');
        var s = arr[arr.length - 1].split('.');
        if (s[s.length - 1] === 'json' && s[s.length - 2] === 'w20') {
            s.splice(-2, 2);
            return s.join('.');
        }
        return s
    }

    var originalManifest = require('./w20.app.json'),
        buildManifest = {};

    for (var key in originalManifest) {
        if (originalManifest.hasOwnProperty(key)) {
            buildManifest[buildKey(key)] = originalManifest[key];
        }
    }

    fs.writeFileSync('dist/w20.app.json', JSON.stringify(buildManifest));
});

gulp.task('build', ['clean', 'bower', 'ts-compile', 'less-compile', 'test', 'copy', 'optimize', 'replace-index', 'replace-manifest']);
gulp.task('build-src', ['clean', 'bower', 'ts-compile', 'less-compile', 'test', 'copy', 'replace-manifest']);
gulp.task('default', ['build']);

// While in dev
gulp.task('watch', ['ts-compile'], function () {
    gulp.watch(['hub/**/*.ts'], ['ts-compile']);
});