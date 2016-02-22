// jshint ignore: start
const gulp = require('gulp');
const typescript = require('gulp-typescript');
const bower = require('gulp-bower');
const jshint = require('gulp-jshint');
const del = require('del');
const tscConfig = require('./tsconfig.json');
const sourcemaps = require('gulp-sourcemaps');
var Server = require('karma').Server;

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
            'index.html',
            'w20.app.json'],
        { base : './' })
        .pipe(gulp.dest('dist'));
});

gulp.task('test', ['copy'], function (done) {
    new Server({
        configFile: __dirname + '/karma.conf.js',
        singleRun: true
    }, done).start();
});


gulp.task('watch', ['compile'], function () {
    gulp.watch(['hub/**/*.ts'], ['compile']);
});

gulp.task('build', ['clean', 'bower', 'compile', 'copy', 'test']);
gulp.task('default', ['build']);