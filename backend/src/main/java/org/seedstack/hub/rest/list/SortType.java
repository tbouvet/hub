package org.seedstack.hub.rest.list;

public enum SortType {
    DATE,
    NAME,
    STARS;

    public static SortType fromOrDefault(String sort) {
        if (sort != null && !sort.equals("")) {
            try {
                return SortType.valueOf(sort.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The sort value can only be: DATE, NAME or STARS");
            }
        } else {
            return SortType.NAME;
        }
    }
}
