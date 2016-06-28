class DirectiveFactory {
    static getFactoryFor<T extends ng.IDirective>(classType: Function): ng.IDirectiveFactory {
        var factory = (...args): T => {
            var directive = <any> classType;
            return new (directive.bind(directive, ...args));
        };
        factory.$inject = classType.$inject;
        return factory;
    }
}