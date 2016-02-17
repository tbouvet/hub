/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global module: false, grunt: false, process: false */
module.exports = function (grunt) {
    'use strict';

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        clean: [
            'bower_components/**',
            'dist/**',
            'coverage/**'
        ],
        jshint: {
            all: {
                src: ['hub/modules/**/*.js']
            }
        },
        bower: {
            install: {
                options: {
                    copy: false
                }
            }
        },
        karma: {
            test: {
                configFile: 'karma.conf.js',
                singleRun: true
            },
            watch: {
                configFile: 'karma.conf.js',
                autoWatch: true
            }
        },
        connect: {
            server: {
                options: {
                    port:  grunt.option('port') || 8888,
                    base: '.',
                    keepalive: true
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-coveralls');
    grunt.loadNpmTasks('grunt-bower-task');
    grunt.loadNpmTasks('grunt-contrib-connect');

    grunt.registerTask('default', ['jshint', 'bower', 'karma:test'], null);
};
