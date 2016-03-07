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
        "create": {
            "href": "/user/components"
        },
        "popular": {
            "href": "/popular"
        },
        "recent": {
            "href": "/recent"
        }
    }
};

module.exports = home;