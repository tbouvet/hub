class Card {
    id: string;
    name: string;
    summary: string;
    owner: string;
    stars: number;
    icon: string;
}

class Component extends Card {
    version: string;
    state: string;
    readme: string;
    images: string[];
    docs: string[];
    maintainers: string[];
}