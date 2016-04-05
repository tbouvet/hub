var DirectiveFactory = (function () {
    function DirectiveFactory() {
    }
    DirectiveFactory.getFactoryFor = function (classType) {
        var factory = function () {
            var args = [];
            for (var _i = 0; _i < arguments.length; _i++) {
                args[_i - 0] = arguments[_i];
            }
            var directive = classType;
            return new (directive.bind.apply(directive, [directive].concat(args)));
        };
        factory.$inject = classType.$inject;
        return factory;
    };
    return DirectiveFactory;
}());

//# sourceMappingURL=directive-factory.js.map
