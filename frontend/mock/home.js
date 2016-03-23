var home = {
    "resources": {
        "components": {
            "href-template": "/components{?search,sort,pageIndex,pageSize}",
            "href-vars": {
                "search": "/doc/components/param/search",
                "sort": "/doc/components/param/sort",
                "page": "/doc/components/param/page",
                "show": "/doc/components/param/show"
            }
        },
        "component": {
            "href-vars": {
                "componentId": "componentId"
            },
            "hints": {
                "allow": [
                    "GET"
                ],
                "formats": {
                    "application/hal+json": "",
                    "application/json": ""
                }
            },
            "href-template": "/components/{componentId}"
        },
        "popular": {
            "href": "/popular"
        },
        "recent": {
            "href": "/recent"
        },
        "user/components": {
            "href-template": "/user/components{?search,sort,pageIndex,pageSize}",
            "href-vars": {
                "search": "/doc/components/param/search",
                "sort": "/doc/components/param/sort",
                "page": "/doc/components/param/page",
                "show": "/doc/components/param/show"
            }
        },
        "stars": {
            "href-template": "/user/stars{?search,sort,pageIndex,pageSize}",
            "href-vars": {
                "search": "/doc/components/param/search",
                "sort": "/doc/components/param/sort",
                "page": "/doc/components/param/page",
                "show": "/doc/components/param/show"
            }
        }
    }
};

module.exports = home;