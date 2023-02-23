package dk.easv.dataaccess;

import dk.easv.entities.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DataAccessManager {
    private HashMap<Integer, User> users = new HashMap<>();
    private HashMap<Integer, Movie> movies = new HashMap<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();



    private List<String> moviePosters = new ArrayList<>();

    private String postersDirectory = "src/resources/posters";


    // Loads all data from disk and stores in memory
    // For performance, data is only updated if updateCacheFromDisk() is called
    public DataAccessManager() {
        updateCacheFromDisk();
    }

    public Map<Integer, User> getAllUsers() {
        return users;
    }

    public Map<Integer, Movie> getAllMovies() {
        return movies;
    }
    public ArrayList<String> getDescriptions() {return descriptions;}
    public List<Rating> getAllRatings(){
        return ratings;
    }
    public List<String> getMoviePosters() {return moviePosters;}

    public void updateCacheFromDisk(){
        loadAllRatings();
        loadAllPosters();
        loadAllDescriptions();
    }

    private void loadAllPosters() {
        File dir = new File(postersDirectory);

        Collection<String> posters =new ArrayList<String>();

        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {
                    posters.add(file.getAbsolutePath());
                }
            }
        }
        moviePosters.addAll(posters);
    }

    private void loadAllDescriptions() {
        try {
            List<String> descriptionLines = Files.readAllLines(Path.of("data/descriptions.txt"));
            Collection<String> descriptionsColl = new ArrayList<>();
            for (String descriptionLine : descriptionLines) {
                String[] split = descriptionLine.split("!");
                Description description = new Description(Integer.parseInt(split[0]), split[1]);
                descriptionsColl.add(description.getDescription());
            }
            descriptions.addAll(descriptionsColl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadAllMovies() {
        try {
            List<String> movieLines = Files.readAllLines(Path.of("data/movie_titles.txt"));
            for (String movieLine : movieLines) {
                String[] split = movieLine.split(",");
                Movie movie = new Movie(Integer.parseInt(split[0]), split[2], Integer.parseInt(split[1]));
                movies.put(movie.getId(), movie);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllUsers() {
        try {
            List<String> userLines = Files.readAllLines(Path.of("data/users.txt"));
            for (String userLine : userLines) {
                String[] split = userLine.split(",");
                User user = new User(Integer.parseInt(split[0]), split[1]);
                users.put(user.getId(), user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads all ratings, users and movies must be loaded first
    // Users holds a list of ratings and movies holds a list of ratings
    private void loadAllRatings() {
        loadAllMovies();
        loadAllUsers();
        try {
            List<String> ratingLines = Files.readAllLines(Path.of("data/ratings.txt"));
            for (String ratingLine : ratingLines) {
                String[] split = ratingLine.split(",");
                int movieId = Integer.parseInt(split[0]);
                int userId = Integer.parseInt(split[1]);
                int rating = Integer.parseInt(split[2]);
                Rating ratingObj = new Rating(users.get(userId), movies.get(movieId), rating);
                ratings.add(ratingObj);
                users.get(userId).getRatings().add(ratingObj);
                movies.get(movieId).getRatings().add(ratingObj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
