package com.varun.selfstudy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.varun.selfstudy.dao.AuthorRepository;
import com.varun.selfstudy.dao.BookRepository;
import com.varun.selfstudy.vo.Author;
import com.varun.selfstudy.vo.Book;

@SpringBootApplication
public class BetterReadsDataLoaderApplication {

    @Resource
    private AuthorRepository authorRepository;

    @Resource
    private BookRepository bookRepository;

    @Resource(name = "jsonObjectMapper")
    private ObjectMapper objectMapper;

    @Value("${better.reads.data.dump.authors}")
    private String authorsFilePath;

    @Value("${better.reads.data.dump.works}")
    private String worksFilePath;

    public static void main(String[] args) {
        SpringApplication.run(BetterReadsDataLoaderApplication.class, args);
    }

    @PostConstruct
    public void initialize() {
        System.out.println("Application started.");
//        Author author = new Author();
//        author.setId("123");
//        author.setName("V Sharma");
//        author.setPersonalName("Varun Sharma");
//        authorRepository.save(author);
        initAuthors();
        initWorks();
    }

    private void initAuthors() {
        Path filePath = Paths.get(authorsFilePath);
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                try {
                    // Read and parse line
                    String jsonString = line.substring(line.indexOf("{"));
                    JsonNode jsonNode = objectMapper.readTree(jsonString);

                    // Construct author object
                    Author author = new Author();
                    author.setName(getStringValue(jsonNode, "name"));
                    author.setPersonalName(getStringValue(jsonNode, "personal_name"));
                    String authorId = getStringValue(jsonNode, "key");
                    authorId = authorId.replace("/authors/", "");
                    author.setId(authorId);

                    // Save author object
                    authorRepository.save(author);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void initWorks() {
        Path filePath = Paths.get(worksFilePath);
        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                try {
                    // Read and parse the line
                    String jsonString = line.substring(line.indexOf("{"));
                    JsonNode jsonNode = objectMapper.readTree(jsonString);

                    // construct book object
                    Book book = new Book();

                    String bookId = getStringValue(jsonNode, "key");
                    bookId = bookId.replace("/works/", "");
                    book.setId(bookId);

                    book.setName(getStringValue(jsonNode, "title"));
                    book.setDescription(getStringValue(jsonNode, "description"));

                    if (jsonNode.has("created")) {
                        String publishedDateStr = getStringValue(jsonNode.get("created"), "value");
                        book.setPublishedDate(LocalDate.parse(publishedDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }

                    if (jsonNode.has("covers")) {
                        List<String> coverIds = new ArrayList<>();
                        ArrayNode arrayNode = (ArrayNode) jsonNode.get("covers");
                        for (int i = 0; i < arrayNode.size(); i++) {
                            coverIds.add(arrayNode.get(i).asText());
                        }
                        book.setCoverIds(coverIds);
                    }

                    if (jsonNode.has("authors")) {
                        List<String> authorIds = new ArrayList<>();
                        ArrayNode arrayNode = (ArrayNode) jsonNode.get("authors");
                        for (int i = 0; i < arrayNode.size(); i++) {
                            String authorId = getStringValue(arrayNode.get(i).get("author"), "key");
                            authorId = authorId.replace("/authors/", "");
                            authorIds.add(authorId);
                        }
                        book.setAuthorIds(authorIds);

                        List<String> authorNames = authorIds.stream().map(authorId -> {
                            return authorRepository.findById(authorId);
                        }).map(optionalAuthor -> {
                            if (!optionalAuthor.isPresent()) {
                                return "Unknown Author";
                            }
                            return optionalAuthor.get().getName();
                        }).collect(Collectors.toList());

                        book.setAuthorNames(authorNames);
                    }
                    // save
                    bookRepository.save(book);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getStringValue(JsonNode jsonNode, String key) {
        if (jsonNode.has(key)) {
            return jsonNode.get(key).asText();
        }
        return "";
    }
}
