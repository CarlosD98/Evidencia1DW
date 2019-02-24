

package Models;

import javax.json.*;
/**
 *
 * @author Usuario
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable{
    private String openLibraryId;
    private String author;
    private String title;

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    // Get medium sized book cover from covers API
    public String getCoverUrl() {
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-M.jpg?default=false";
    }

    // Get large sized book cover from covers API
    public String getLargeCoverUrl() {
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }
    public static Book fromJson(JsonObject jsonObject) {
        Book book = new Book();
        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if (jsonObject.containsValue("cover_edition_key"))  {
                book.openLibraryId = jsonObject.getString("cover_edition_key");
            } else if(jsonObject.containsValue("edition_key")) {
                final JsonArray ids = jsonObject.getJsonArray("edition_key");
                book.openLibraryId = ids.getString(0);
            }
            book.title = jsonObject.containsValue("title_suggest") ? jsonObject.getString("title_suggest") : "";
            book.author = getAuthor(jsonObject);
        } catch (JsonException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JsonObject jsonObject) {
        try {
            final JsonArray authors = jsonObject.getJsonArray("author_name");
            int numAuthors = authors.size();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return String.format(", ", authorStrings);
        } catch (JsonException e) {
            return "";
        }
    }
    // Decodes array of book json results into business model objects
    public static ArrayList<Book> fromJson(JsonArray jsonArray) {
        ArrayList<Book> books = new ArrayList<Book>(jsonArray.size());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject bookJson = null;
            try {
                bookJson = jsonArray.getJsonObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Book book = Book.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }
}
