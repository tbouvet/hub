// jshint ignore: start
const gulp = require('gulp');
const typescript = require('gulp-typescript');
const bower = require('gulp-bower');
const jshint = require('gulp-jshint');
const del = require('del');
const tscConfig = require('./tsconfig.json');
const sourcemaps = require('gulp-sourcemaps');
const htmlreplace = require('gulp-html-replace');
var Server = require('karma').Server;
require('gulp-grunt')(gulp);

gulp.task('clean', function () {
    return del('dist/**/*');
});

gulp.task('bower', function() {
    return bower();
});

gulp.task('compile', function () {
    return gulp
        .src(tscConfig.files)
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('hub'));
});

gulp.task('copy', ['bower', 'clean', 'compile'], function () {
    return gulp.src([
            'bower_components/**/*',
            'hub/**/*',
            '!hub/**/*.ts',
            '!hub/**/*.js',
            'w20.app.json'],
        { base : './' })
        .pipe(gulp.dest('dist'));
});

gulp.task('test', ['bower', 'compile'], function (done) {
    new Server({
        configFile: __dirname + '/karma.conf.js',
        singleRun: true
    }, done).start();
});

gulp.task('optimize', ['compile', 'copy'], function () {
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

gulp.task('build', ['clean', 'bower', 'compile', 'test', 'copy', 'optimize', 'replace-index']);
gulp.task('default', ['build']);

// While in dev
gulp.task('watch', ['compile'], function () {
    gulp.watch(['hub/**/*.ts'], ['compile']);
});