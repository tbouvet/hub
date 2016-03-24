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

gulp.task('bower', function () {
    return bower();
});

gulp.task('copy', ['clean', 'bower'], function () {
    return gulp.src([
            'bower_components/**/*',
            'src/**/*',
            '!src/**/*.ts',
            '!src/**/*.less',
            'w20.app.json'],
        {base: './'})
        .pipe(gulp.dest('dist'));
});

gulp.task('less-compile', ['copy'], function () {
    return gulp.src('src/style/**/*.less')
        .pipe(less())
        .pipe(gulp.dest('dist/src/style'));
});

gulp.task('ts-compile', ['copy'], function () {
    return gulp
        .src(tscConfig.files)
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('dist/src/'));
});

gulp.task('test', ['ts-compile'], function (done) {
    new Server({
        configFile: __dirname + '/karma.conf.js',
        singleRun: true
    }, done).start();
});

gulp.task('optimize', ['ts-compile', 'less-compile'], function () {
    return gulp.start('grunt-default');
});

gulp.task('replace-index', ['copy'], function () {
    gulp.src('index.html')
        .pipe(htmlreplace({
            replaceLoader: {
                src: 'hub.min.js',
                tpl: '<script src="%s"></script>'
            },
            replaceConfig: {
                src: [[
                    '${restPath}/seed-w20/application/configuration',
                    '${applicationVersion}',
                    '${timeout}',
                    '${corsWithCredentials}'
                ]],
                tpl: '<html data-w20-app="%s" data-w20-app-version="%s" data-w20-timeout="%s" data-w20-cors-with-credentials="%s">'
            },
            replaceTitle: {
                src: ['${applicationTitle}'],
                tpl: '<title>%s</title>'
            }
        }))
        .pipe(gulp.dest('dist/'));
});

gulp.task('remove-unused', ['copy', 'test', 'optimize'], function () {
    return del([
        'dist/w20.app.json'
    ]);
});

gulp.task('build', ['clean', 'bower', 'copy', 'ts-compile', 'less-compile', 'test', 'optimize', 'replace-index', 'remove-unused']);
gulp.task('default', ['build']);


// Development (Seed backend)

gulp.task('clean-dev', function () {
    return del('../backend/src/main/resources/META-INF/resources/**/*');
});

gulp.task('copy-dev', ['bower', 'clean'], function () {
    return gulp.src([
            'bower_components/**/*',
            'src/**/*',
            '!src/**/*.ts',
            '!src/**/*.less',
            '!w20.app.json',
            '!index.html'],
        {base: './'})
        .pipe(gulp.dest('../backend/src/main/resources/META-INF/resources'));
});

gulp.task('less-compile-dev', ['copy-dev'], function () {
    return gulp.src('src/style/**/*.less')
        .pipe(less())
        .pipe(gulp.dest('../backend/src/main/resources/META-INF/resources/src/style'));
});

gulp.task('ts-compile-dev', ['copy-dev'], function () {
    return gulp
        .src(tscConfig.files)
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('../backend/src/main/resources/META-INF/resources/src/'));
});

gulp.task('replace-index-dev', function () {
    gulp.src('index.html')
        .pipe(htmlreplace({
            replaceLoader: {
                src: [['bower_components/w20/modules/w20', 'bower_components/requirejs/require.js']],
                tpl: '<script data-main="%s" src="%s"></script>'
            },
            replaceConfig: {
                src: [[
                    '${restPath}/seed-w20/application/configuration',
                    '${applicationVersion}',
                    '${timeout}',
                    '${corsWithCredentials}'
                ]],
                tpl: '<html data-w20-app="%s" data-w20-app-version="%s" data-w20-timeout="%s" data-w20-cors-with-credentials="%s">'
            },
            replaceTitle: {
                src: ['${applicationTitle}'],
                tpl: '<title>%s</title>'
            }
        }))
        .pipe(gulp.dest('../backend/src/main/resources/META-INF/resources'));
});

gulp.task('watch-dev', [], function () {
    gulp.watch(['src/**/*'], ['ts-compile-dev', 'less-compile-dev', 'copy-dev']);
});

gulp.task('build-dev', ['clean-dev', 'bower', 'copy-dev', 'ts-compile-dev', 'less-compile-dev', 'replace-index-dev']);

// Development (NodeJS mock backend)

gulp.task('copy-node', ['bower', 'clean'], function () {
    return gulp.src([
            'bower_components/**/*',
            'src/**/*',
            '!src/**/*.ts',
            '!src/**/*.less',
            'w20.app.json',
            'index.html'],
        {base: './'})
        .pipe(gulp.dest('dist'));
});

gulp.task('less-compile-node', ['copy-node'], function () {
    return gulp.src('src/style/**/*.less')
        .pipe(less())
        .pipe(gulp.dest('dist/src/style'));
});

gulp.task('ts-compile-node', ['copy-node'], function () {
    return gulp
        .src(tscConfig.files)
        .pipe(sourcemaps.init())
        .pipe(typescript(tscConfig.compilerOptions))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('dist/src/'));
});

gulp.task('build-node', ['clean', 'bower', 'copy-node', 'ts-compile-node', 'less-compile-node']);

gulp.task('watch-node', [], function () {
    gulp.watch(['src/**/*'], ['ts-compile-node', 'less-compile-node', 'copy-node']);
});