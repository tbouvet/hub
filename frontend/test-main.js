/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
var tests = [];

for (var file in window.__karma__.files) {
    if (/.spec\.js$/.test(file)) {
        tests.push(file);
    }
}

window.w20 = {
    configuration: {
        '/base/dist/bower_components/w20/w20-core.w20.json': {
            modules: {
                application: {
                    id: 'w20-test',
                    home: '/test'
                }
            },
            vars: {
                'components-path': '/base/dist/bower_components'
            }
        },
        '/base/dist/bower_components/w20-material/w20-material.w20.json': {
            vars: {
                'components-path': '/base/dist/bower_components'
            }
        }
    },
    deps: tests,
    callback: window.__karma__.start
};

requirejs.config({
    paths: {
        '{angular-mocks}': '/base/dist/bower_components/angular-mocks',
        '{hub}': '/base/dist/src',
        '{angular-messages}': '/base/dist/bower_components/angular-messages'
    },
    shim: {
        '{angular-mocks}/angular-mocks': [ '{angular}/angular' ],
        '{angular-messages}/angular-messages': ['{angular}/angular']
    }
});

requirejs([ '/base/dist/bower_components/w20/modules/w20.js' ]);



