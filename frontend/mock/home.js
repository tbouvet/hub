var home = {
    "resources": {
        "components": {
            "href-template": "/api/components{?search,sort,pageIndex,pageSize}",
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
            "href-template": "/api/components/{componentId}"
        },
        "popular": {
            "href": "/api/popular"
        },
        "recent": {
            "href": "/api/recent"
        },
        "user/components": {
            "href-template": "/api/user/components{?search,sort,pageIndex,pageSize}",
            "href-vars": {
                "search": "/doc/components/param/search",
                "sort": "/doc/components/param/sort",
                "page": "/doc/components/param/page",
                "show": "/doc/components/param/show"
            }
        },
        "stars": {
            "href-template": "/api/user/stars{?search,sort,pageIndex,pageSize}",
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