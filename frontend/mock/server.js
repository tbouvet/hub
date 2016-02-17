/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
var express = require('express');
var cards = require('./components-card');
var app = express();

app.use(express.static(__dirname + '/../webapp'));

app.get('/components', function (req, res) {
    res.json(cards);
});

app.listen(3000, function () {
    console.log('Hub app listening on port 3000!');
});

