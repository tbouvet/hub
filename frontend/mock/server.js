/*
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

const dist = process.argv.slice(2)[0];

const express = require('express');
const bodyParser = require("body-parser");
const app = express();
const fs = require('fs');
var home = require('./home');
var cards = require('./components-card');
var component = require('./component');
var userComponent = require('./user-components');

function is(req, mimeType) {
    return req.headers.accept === mimeType;
}

function isHal(req) {
    return is(req, 'application/hal+json');
}

function isJsonHome(req) {
    return is(req, 'application/json-home, application/json');
}

function setHalResponse(res) {
    res.set({'content-type': 'application/hal+json'});
}

function sendCards(cards, index, size, res) {
    setHalResponse(res);
    index = index ? index : 0;
    size = size ? size : 10;
    var list = [].concat(cards.slice(Number(index) * Number(size), Number(index) * Number(size) + Number(size)));
    res.status(200).send(new Buffer(JSON.stringify({
        _embedded: {
            components: list
        }
    })));
}

function search(array, query) {
    if (query) {
        return array.filter(card => {
            query = query.toLowerCase();
            return card.name.toLowerCase().search(query) !== -1 || card.summary.toLowerCase().search(query) !== -1;
        })
    } else {
        return array;
    }

}

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

app.get('/', (req, res, next) => {
    return isJsonHome(req) ? res.json(home) : next();
});

app.get('/popular', (req, res, next) => {
    return isHal(req) ? sendCards(cards, 0, 6, res) : next();
});

app.get('/recent', (req, res, next) => {
    return isHal(req) ? sendCards(cards, 0, 6, res) : next();
});

app.get('/components', (req, res) => {
    var filteredCards = search(cards, req.query.search);
    sendCards(filteredCards, req.query.pageIndex, req.query.pageSize, res);
});

var componentId = 0;
app.post('/components', (req, res) => {
    // Mock 404
    //res.status(404).send();
    var newComponent = cards[0];
    newComponent.name = "Component"  + componentId++;
    setTimeout(() => { res.json(newComponent); }, 5000);
});

app.get('/components/:componentId', (req, res, next) => {
    component.id = req.params.componentId;
    component.name = 'Name ' + req.params.componentId;
    res.json(component);
});

app.get('/components/*/files/images/*', (req, res, next) => {
    var img = fs.readFileSync('../hub/frontend/mock/images/ubuntu.png');
    res.writeHead(200, {'Content-Type': 'image/png' });
    res.end(img, 'binary');
});

app.get('/components/*/files/README.md', (req, res, next) => {
    var readme = fs.readFileSync('../hub/frontend/mock/docs/readme.html');
    res.writeHead(200, {'Content-Type': 'text/html' });
    res.end(readme);
});

app.get('/user/components', (req, res) => {
    sendCards(userComponent, req.query.pageIndex, req.query.pageSize, res);
});

app.get('/user/stars', (req, res) => {
    sendCards(userComponent, req.query.pageIndex, req.query.pageSize, res);
});

if (dist === 'dist') {
    app.use(express.static(__dirname + '/../dist'));
} else {
    app.use(express.static(__dirname + '/../.'));
}

app.listen(3000, () => {
    console.log('Hub app listening on port 3000!');
});

