package domain;

public class FilmData {

    private final String characterName;
    private final String filmName;

    public FilmData(String characterName, String filmName) {
        this.characterName = characterName;
        this.filmName = filmName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getFilmName() {
        return filmName;
    }

}
