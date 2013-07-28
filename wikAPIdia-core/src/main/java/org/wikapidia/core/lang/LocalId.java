package org.wikapidia.core.lang;

import org.wikapidia.core.model.LocalPage;

/**
 * A language-specific id.
 * @author Shilad Sen
 */
public class LocalId {

    /**
     * Maximum language and page ids that can be packed into an int.
     */
    private static final int MAX_PACKED_LANGUAGE_ID = (1<<6) - 1;   // 6 bits of precision
    private static final int MAX_PACKED_ID = (1<<26) - 1;           // 26  bits of precision

    private Language language;
    private int id;

    public LocalId(Language language, int id) {
        this.language = language;
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public int getId() {
        return id;
    }

    public LocalPage asLocalPage(){
        return new LocalPage(
                language,
                id,
                null,
                null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalId localId = (LocalId) o;

        return id == localId.id && language.equals(localId.language);

    }

    @Override
    public int hashCode() {
        int result = language.hashCode();
        result = 31 * result + id;
        return result;
    }

    public int toInt() {
        // crazy scheme that breaks if languages "in the tail" get page ids that are too big
        // TODO: this could be smarter
        if (language.getId() > MAX_PACKED_LANGUAGE_ID) {
            throw new IllegalStateException("cannot pack language ids >= " + MAX_PACKED_LANGUAGE_ID + " into an int");
        }
        if (id > MAX_PACKED_ID) {
            throw new IllegalStateException("cannot pack ids >= " + MAX_PACKED_ID + " into an int");
        }
        return (language.getId() << 26) | id;
    }

    public static LocalId fromInt(int packed) {
        int languageId = packed >>> 26;
        if (languageId < 0 || languageId > MAX_PACKED_ID)
            throw new IllegalArgumentException("illegal languageId: " + languageId + " in " + packed);
        int id = packed & MAX_PACKED_ID;
        return new LocalId(Language.getById(languageId), id);
    }

    public boolean canPackInInt() {
        return (language.getId() <= MAX_PACKED_LANGUAGE_ID && id <= MAX_PACKED_ID);
    }
}
