import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.lang.*;

// Class name changed to match the filename Double_Feature.java
public class Double_Feature {

  // Main method remains the same
  public static void main (String[] args) throws IOException{
    // --- Variable Declarations ---
    Scanner in = new Scanner(System.in); // For user input
    // Flags to control program flow
    Boolean searching = true; // Main loop for finding a theatre
    Boolean refresh = true;   // Loop for refreshing theatre search results
    Boolean cityCheck = false; // Flag if user entered a city
    Boolean zipCheck = false;  // Flag if user entered a zip code
    Boolean movieNum = false; // Unused flag?
    Boolean joining = false;  // Flag for joining parts of scraped theatre names
    Boolean secondPhase = true; // Flag to control if Fandango scraping proceeds
    // Data variables
    int zipCode = 0;
    int length = 0; // Length of zip code input
    int choiceNum, optionNum; // Unused variables?
    double actualTime2 = 0; // Time of the movie converted to decimal number (used later)
    String chosenTheatre = ""; // Initialize chosenTheatre
    String chosenTheatre2 = ""; // <<< DECLARED HERE (for original name display)
    String city = "", timeFrame = "", actualTime = "", movies = "", duration = "", finalDuration = "", movieOne = "", movieOneTime = "", reply = ""; // Initialize string variables
    // ArrayLists to store scraped data
    ArrayList<String> allTimes = new ArrayList<String>(); // Stores formatted string "[duration] Movie Title - Showtime"
    ArrayList<String> justTime = new ArrayList<String>(); // Stores just the showtime string (e.g., "7:30pm")
    ArrayList<String> justMovies = new ArrayList<String>(); // Stores just the movie title
    ArrayList<String> filmDurationString = new ArrayList<String>(); // Stores duration as formatted string (e.g., "1h 30m")
    ArrayList<String> movieTheatres = new ArrayList<String>(); // Stores names of theatres found near the user
    ArrayList<Double> filmDuration = new ArrayList<Double>(); // Stores duration as decimal hours (e.g., 1.5)
    ArrayList<Double> filmStartTime = new ArrayList<Double>(); // Stores start time as decimal hours (e.g., 19.5 for 7:30pm)
    String dateToUse = ""; // Variable to store the date used for showtimes

    // --- Get Current Date ---
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    // Extract parts of the date (Note: This assumes the format is consistent)
    String year = timeStamp.substring( 2, 4); // Gets 2-digit year
    // int years = Integer.parseInt(year); // Unused integer conversion?
    String month = timeStamp.substring( 4, 6);
    int months = Integer.parseInt(month); // Used for validation later maybe?
    String day = timeStamp.substring( 6, 8);
    int days = Integer.parseInt(day); // Used for validation later maybe?
    String date = month+"/"+day+"/"+year; // Format date as MM/DD/YY
    System.out.println("Today's date: " + date); // Print today's date

    // --- Main Loop: Find Theatre ---
    while (searching){
      System.out.println("Enter Zip Code or Name of City:");
      reply = in.next(); // Get user input for location
      refresh = true; // Reset refresh flag for inner loop
      movieTheatres.clear(); // Clear previous theatre list if looping

      // --- Inner Loop: Validate Input & Scrape Theatres ---
      while (refresh){
        // Check if input is numeric (potential zip code)
        if(reply.matches("^\\d{5}$")) { // More specific zip code check
          zipCode = Integer.parseInt(reply);
          length = reply.length();
          zipCheck = true;
          cityCheck = false; // Ensure cityCheck is false if zip is entered
        }
        else{ // Assume it's a city name
          city = reply; // Assign reply to city variable
          cityCheck = true;
          zipCheck = false; // Ensure zipCheck is false
          length = 0; // Reset length if it's a city
        }

        // Proceed if input is a 5-digit zip or a city name
        if (zipCheck || cityCheck){ // Simplified condition
          System.out.println("Searching for theatres near: " + reply);
          // --- Bing Scraping for Theatres ---
          InputStream is1 = null; // Initialize to null
          Scanner insert = null; // Initialize to null
          try { // Wrap network operations in try-catch
              // Ensure 'reply' is initialized before use
              if (reply == null || reply.isEmpty()) {
                 System.err.println("Error: Location input is empty.");
                 searching = true; // Go back to ask for location
                 refresh = false; // Exit inner loop
                 continue; // Skip to next outer loop iteration
              }
              URL url1 = new URL("https://www.bing.com/search?q=theatres+near+" + URLEncoder.encode(reply, "UTF-8")); // Encode reply for URL
              URLConnection con1 = url1.openConnection();
              con1.setConnectTimeout(10000); // 10 second timeout
              con1.setReadTimeout(10000); // 10 second timeout
              con1.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36"); // Updated User-Agent
              is1 = con1.getInputStream();
              insert = new Scanner(new InputStreamReader(is1));

              // Boolean reading = true; // Unused flag?
              String theatre = ""; // Temp string for scraped line
              // String theatre2 = ""; // Temp string for processing name - Removed in refactor
              String theatre3 = ""; // Holds the final cleaned theatre name
              int count = 0; // Counter for found theatres
              Boolean found2 = false; // Flag if any theatre was found

              // Loop through the HTML source from Bing
              // This relies heavily on specific, outdated HTML structure
              while ((insert.hasNext() && count <= 7)){ // Limit to ~8 theatres
                joining = false; // Reset joining flag
                Boolean found = false; // Flag if a potential theatre line is found
                String currentWord = insert.next(); // Read next word/token

                // VERY FRAGILE: Checks for specific HTML patterns preceding theatre names (these likely changed years ago)
                // This scraping logic is highly likely to fail. Modern approach needed (Jsoup/Selenium).
                if (currentWord.contains("class=\"b_entityTitle\"")) { // Example of a potential class (likely outdated too)
                     theatre = ""; // Reset theatre string
                     while(insert.hasNext() && !theatre.contains("</a>")){ // Read until closing anchor tag
                         theatre += insert.next() + " ";
                     }
                     // Basic extraction attempt (needs refinement based on actual HTML)
                     if(theatre.contains(">") && theatre.contains("</a")){
                         try { // Add try-catch for robustness
                             theatre3 = theatre.substring(theatre.indexOf(">") + 1, theatre.indexOf("</a"));
                             // Basic cleaning (remove potential HTML tags within name)
                             theatre3 = theatre3.replaceAll("<[^>]*>", "").trim();
                             found = true;
                             found2 = true; // Found at least one
                             joining = false; // Don't need old joining logic
                             count++;
                             // Clean up potential HTML entities
                             if (theatre3.contains("&amp;")){ theatre3 = theatre3.replaceAll("&amp;", "&");}
                             if (theatre3.contains("&#39;")){ theatre3 = theatre3.replaceAll("&#39;", "'");}
                             // Add more entity replacements if needed
                             movieTheatres.add(theatre3);
                         } catch (StringIndexOutOfBoundsException e) {
                             // Ignore if substring fails
                             System.err.println("Warning: Could not parse potential theatre name from: " + theatre);
                         }
                     }
                }
              } // End of Bing HTML parsing loop
          } catch (MalformedURLException e) {
              System.err.println("Error: Invalid URL created for Bing search.");
              secondPhase = false; // Cannot proceed
          } catch (IOException e) {
              System.err.println("Error connecting to or reading from Bing: " + e.getMessage());
              secondPhase = false; // Cannot proceed
          } finally {
              // Ensure resources are closed
              if (insert != null) insert.close();
              if (is1 != null) try { is1.close(); } catch (IOException e) { /* ignore */ }
          } // END OF TRY-CATCH-FINALLY for Bing theatre search


          // --- Display Found Theatres and Get User Choice ---
          if (!movieTheatres.isEmpty()){ // Check if list is not empty
            System.out.println("\nThese are the theatres found near you:\n");
            for (int m = 0; m < movieTheatres.size(); m++){ // Use movieTheatres.size()
              System.out.println((m+1) + ". " + movieTheatres.get(m));
            }

            System.out.println((movieTheatres.size() + 1) + ". Refresh results"); // Use dynamic number
            System.out.println("0. Try again with another location");
            System.out.println("\nChoose the corresponding number for the theatre:");
            String answer = in.next();
            int answer2 = -1; // Initialize to invalid value
            try {
                 answer2 = Integer.parseInt(answer); // Try parsing input to integer
            } catch (NumberFormatException e) {
                 System.out.println("Invalid input. Please enter a number.");
                 refresh = true; // Keep refresh=true to loop again
                 continue; // Skip rest of the loop iteration
            }

            if (answer2 > 0 && answer2 <= movieTheatres.size()){ // Valid theatre choice
              System.out.println("You chose: " + movieTheatres.get(answer2-1));
              chosenTheatre = movieTheatres.get(answer2-1); // Store chosen theatre name
              refresh = false; // Exit inner loop
              searching = false; // Exit outer loop (theatre found)
            }
            else if (answer2 == (movieTheatres.size() + 1)){ // Refresh option
              refresh = true; // Stay in inner loop, will clear list at top
            }
            else if (answer2 == 0){ // Try another location
              refresh = false; // Exit inner loop
              searching = true; // Stay in outer loop to ask for location again
            }
            else { // Invalid number
              System.out.println("You did not choose a valid option, try again!");
              refresh = true; // Keep refresh = true to re-display options
            }
          }
          else{ // No theatres found via scraping or error occurred
            System.out.println("No theatres were found for this location using Bing search, or an error occurred. Please try again!");
            refresh = false; // Exit inner loop
            searching = true; // Go back to ask for location again
          }
        }
        else{ // Invalid zip code length (or city check failed somehow, though unlikely)
          System.out.println("The zip code entered is not valid (must be 5 digits). Please try again!");
          refresh = false; // Exit inner loop
          searching = true; // Go back to ask for location again
        }
      } // End of inner loop (refresh)
    } // End of outer loop (searching)

    // --- Phase 2: Find Fandango Link and Scrape Showtimes ---
    InputStream stream = null; // Initialize
    Scanner system = null; // Initialize
    String link = ""; // Initialize link

    if (secondPhase) { // Only proceed if theatre search was potentially successful
        // Prepare theatre name for URL search (replace spaces with '+')
        chosenTheatre2 = chosenTheatre; // <<< ASSIGNMENT REMAINS HERE (Keep original name for display)
        try {
            // Ensure chosenTheatre is not null or empty before encoding
            if (chosenTheatre == null || chosenTheatre.isEmpty()) {
                System.err.println("Error: Theatre name is empty, cannot search Fandango.");
                secondPhase = false; // Cannot proceed
            } else {
                chosenTheatre = URLEncoder.encode(chosenTheatre, "UTF-8"); // Encode theatre name
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding theatre name: " + e.getMessage());
            // Fallback to simple replacement if encoding fails and chosenTheatre is not null
            if (chosenTheatre != null) {
                chosenTheatre = chosenTheatre.replaceAll("\\s+", "+");
            } else {
                secondPhase = false; // Cannot proceed if chosenTheatre was null
            }
        }


        if (secondPhase) { // Check again after potential encoding issues
            System.out.println("\nSearching for Fandango link for: " + chosenTheatre2);
            // --- Bing Scraping for Fandango Link ---
            try { // Wrap network operations
                URL urlSearch = new URL("https://www.bing.com/search?q=" + chosenTheatre + "+fandango");
                URLConnection kon = urlSearch.openConnection();
                kon.setConnectTimeout(10000); // 10 second timeout
                kon.setReadTimeout(10000); // 10 second timeout
                kon.setRequestProperty("User-Agent", "Mozilla/5.0"); // Set User-Agent
                stream = kon.getInputStream();
                system = new Scanner(new InputStreamReader(stream));

                Boolean reading2 = false; // Flag: found Fandango link?
                String lineOne; // Temp string for scraped line

                // Loop through Bing results HTML for Fandango link
                // FRAGILE: Looks for a URL containing "theaterpage" or similar patterns
                while (system.hasNext() && !reading2) {
                  lineOne = system.next();
                  // Updated check for potential Fandango links
                  if (lineOne.contains("href=\"") && lineOne.contains("fandango.com") && (lineOne.contains("theater-page") || lineOne.contains("theaterpage"))) {
                      try {
                          link = lineOne.substring(lineOne.indexOf("href=\"") + 6); // Start after href="
                          link = link.substring(0, link.indexOf("\"")); // End before closing quote "
                          // Basic check if it looks like a valid URL
                          if (link.startsWith("http") && link.contains("fandango.com")) {
                              reading2 = true; // Found a likely link
                              System.out.println("Found potential Fandango link: " + link);
                          } else {
                              link = ""; // Reset if it wasn't a good link
                          }
                      } catch (Exception e) {
                          // Handle potential substring errors if HTML is unexpected
                          link = ""; // Reset on error
                      }
                  }
                } // End while loop for finding link

                if (!reading2) { // If no Fandango link found after searching
                  System.out.println("We were unable to find Fandango showtimes link for this theatre via Bing!");
                  secondPhase = false; // Skip the rest of the process
                }

            } catch (MalformedURLException e) {
                System.err.println("Error: Invalid URL created for Bing Fandango search.");
                secondPhase = false;
            } catch (IOException e) {
                System.err.println("Error connecting to or reading from Bing for Fandango link: " + e.getMessage());
                secondPhase = false;
            } finally {
                if (system != null) system.close();
                if (stream != null) try { stream.close(); } catch (IOException e) { /* ignore */ }
            } // END OF TRY-CATCH-FINALLY for Bing Fandango link search
        } // End inner if(secondPhase)
    } // End outer if(secondPhase) check before Fandango link search


    // --- Get Date for Showtimes ---
    if (secondPhase) { // Only proceed if Fandango link was found
        System.out.println("\nEnter the date for the showtimes (mm/dd/yy format)");
        System.out.println("(Leave blank or enter invalid format to use today's date: " + date + ")");
        in.nextLine(); // Consume the leftover newline from previous in.next()
        String dateInput = in.nextLine().trim();
        dateToUse = date; // Default to today's date

        // Basic validation for MM/DD/YY format
        if (dateInput.matches("\\d{2}/\\d{2}/\\d{2}")) {
            // Could add more robust date validation here
            dateToUse = dateInput;
            System.out.println("Using date: " + dateToUse);
        } else if (!dateInput.isEmpty()) {
            System.out.println("Date format not recognized as mm/dd/yy. Using today's date: " + date);
        } else {
             System.out.println("No date entered. Using today's date: " + date);
        }

      // --- Fandango Scraping for Showtimes ---
      String finalLink = link;
      // Append date parameter if necessary (check Fandango URL structure)
      try { // Add try block around date encoding
          if (!finalLink.contains("?date=") && !dateToUse.isEmpty()) {
              finalLink += "?date=" + URLEncoder.encode(dateToUse, "UTF-8");
          } else if (!dateToUse.isEmpty()) {
              // Replace existing date if needed (more complex logic)
              finalLink = finalLink.replaceAll("date=[^&]*", "date=" + URLEncoder.encode(dateToUse, "UTF-8"));
          }
      } catch (UnsupportedEncodingException e) {
          System.err.println("Error encoding date for URL: " + e.getMessage());
          // Fallback or handle error as needed
      }


      System.out.println("Attempting to fetch showtimes from: " + finalLink);
      InputStream stream2 = null; // Initialize
      Scanner inner = null; // Initialize

      try { // Wrap Fandango connection and scraping
          // Ensure finalLink is not empty
          if (finalLink == null || finalLink.isEmpty()) {
              throw new MalformedURLException("Fandango link is empty.");
          }
          URL urlShowtimes = new URL(finalLink);
          URLConnection kon2 = urlShowtimes.openConnection();
          kon2.setConnectTimeout(15000); // Increased timeout for Fandango
          kon2.setReadTimeout(15000);
          kon2.setRequestProperty("User-Agent", "Mozilla/5.0"); // Set User-Agent
          stream2 = kon2.getInputStream();
          inner = new Scanner(new InputStreamReader(stream2));

          // --- Parsing Fandango HTML (HIGHLY LIKELY BROKEN) ---
          System.out.println("\n--- Showtime Scraping (Using Outdated Logic) ---");
          // Clear previous data before scraping new showtimes
          allTimes.clear();
          justMovies.clear();
          justTime.clear();
          filmDuration.clear();
          filmDurationString.clear();
          filmStartTime.clear();

          String htmlContent = "";
          while(inner.hasNextLine()){
              htmlContent += inner.nextLine() + "\n"; // Read the whole HTML first
          }


          // --- Placeholder for Modern Scraping Logic ---
          System.out.println("WARNING: Showtime scraping logic below is outdated and likely will not find data.");

          // --- Start of Outdated Scraping Logic ---
          Scanner lineScanner = new Scanner(htmlContent); // Scan the stored HTML content
          String line = "", line2 = ""; // Initialize line variables

          while (lineScanner.hasNextLine()) {
              String currentHtmlLine = lineScanner.nextLine();

              // FRAGILE: Looks for a line containing 'itemtype="1"' (likely changed)
              // Using the more specific schema.org type
              if (currentHtmlLine.contains("itemtype=\"http://schema.org/Movie\"")) {
                  String currentMovieTitle = "";
                  // String currentMovieDurationStr = ""; // Not directly used
                  double currentMovieDurationDecimal = 0.0;
                  String currentMovieDurationFormatted = "";

                  // Try to find title and duration within this movie block
                  // This requires knowing the HTML structure around itemtype=Movie
                  // Example (Pure Guesswork):
                  // This inner loop is flawed - need a proper parser
                  int blockCheck = 0; // Limit lines checked within block
                  while(lineScanner.hasNextLine() && blockCheck < 20){ // Limit check depth
                      blockCheck++;
                      String movieLine = lineScanner.nextLine();
                      if (movieLine.contains("itemprop=\"name\"")) {
                          try {
                              currentMovieTitle = movieLine.substring(movieLine.indexOf(">") + 1, movieLine.indexOf("</"));
                              // Clean title
                              if (currentMovieTitle.contains(": An IMAX 3D Experience")){ currentMovieTitle = currentMovieTitle.replaceAll(": An IMAX 3D Experience", " (IMAX)");}
                              if ((currentMovieTitle.contains("2017")||currentMovieTitle.contains("3D")) || (currentMovieTitle.contains("2017")&&currentMovieTitle.contains("3D"))){ currentMovieTitle = currentMovieTitle.replaceAll("2017", ""); currentMovieTitle = currentMovieTitle.replaceAll("[()]", " "); currentMovieTitle = currentMovieTitle.replaceAll("3D", "(3D)");}
                              currentMovieTitle = currentMovieTitle.replaceAll("\\s+$", ""); // Trim trailing space
                              currentMovieTitle = currentMovieTitle.replaceAll("“", "\""); currentMovieTitle = currentMovieTitle.replaceAll("”", "\""); // Replace curly quotes
                          } catch (Exception e) { /* ignore title parse error */ }
                      }
                      if (movieLine.contains("itemprop=\"duration\"")) {
                           try {
                               String durationContent = movieLine.substring(movieLine.indexOf("content=\"") + 9); // Get content="PT..."
                               durationContent = durationContent.substring(0, durationContent.indexOf("\"")); // Extract PT1H30M

                               String tempDur = durationContent.substring(2); // Get part after PT
                               tempDur = tempDur.substring(0, tempDur.indexOf("M")); // Get part before M
                               String hoursStr = "0"; String minsStr = "0";
                               if (tempDur.contains("H")) {
                                   hoursStr = tempDur.substring(0, tempDur.indexOf("H"));
                                   if(tempDur.indexOf("H") + 1 < tempDur.length()) { minsStr = tempDur.substring(tempDur.indexOf("H") + 1); }
                               } else { minsStr = tempDur; }

                               // Convert to display format and decimal hours
                               currentMovieDurationFormatted = hoursStr + "h " + minsStr + "m";
                               double minutes = Double.parseDouble(minsStr);
                               minutes = minutes / 60.0;
                               minutes = Math.round(minutes * 1000.0) / 1000.0;
                               double hours = Double.parseDouble(hoursStr);
                               currentMovieDurationDecimal = hours + minutes; // e.g., 1.5
                           } catch (Exception e) { /* ignore duration parse error */ }
                      }
                      // Look for showtimes associated with this movie block
                      // FRAGILE: Assumes showtimes are in links nearby
                      if (movieLine.contains("itemprop=\"startDate\"")) { // Check for schema.org showtime indication
                           try {
                               // Extract time like "7:30 PM" or "19:30" from content attribute or text
                               // String timeContent = movieLine.substring(movieLine.indexOf("content=\"") + 9);
                               // timeContent = timeContent.substring(0, timeContent.indexOf("\"")); // e.g., "2025-05-01T19:30"
                               // Extract just the time part if needed, or parse the whole thing
                               // For simplicity, let's assume a simpler text extraction for now:
                               String showtimeText = movieLine.substring(movieLine.indexOf(">") + 1, movieLine.indexOf("</")); // e.g., "7:30pm"

                               actualTime = showtimeText.trim(); // The showtime string
                               actualTime2 = minutesToPercent2(actualTime); // Convert to decimal time

                               // Add the found showtime to lists IF title and duration were found
                               if (!currentMovieTitle.isEmpty() && currentMovieDurationDecimal > 0) {
                                   String allShows = "[" + currentMovieDurationFormatted + "] " + currentMovieTitle + " - " + actualTime;
                                   allTimes.add(allShows);
                                   justMovies.add(currentMovieTitle);
                                   justTime.add(actualTime);
                                   filmDuration.add(currentMovieDurationDecimal);
                                   filmDurationString.add(currentMovieDurationFormatted);
                                   filmStartTime.add(actualTime2);
                                   System.out.println("  Found Showtime: " + actualTime + " for " + currentMovieTitle); // Debug
                               }
                           } catch (Exception e) { /* ignore showtime parse error */ }
                      }

                      // Break from inner loop if we detect the end of this movie's block
                      // This requires knowing the HTML structure (e.g., finding the next 'itemtype=Movie' or closing tag)
                      // This inner loop is flawed. Need proper parser.
                      // Placeholder break condition (likely incorrect):
                      if(movieLine.contains("</article>") || movieLine.contains("</div>")){ // Guessing common block tags
                          break;
                      }
                  } // End inner while loop for parsing movie block
              } // End if contains itemtype=Movie
          } // End while lineScanner.hasNextLine()
          lineScanner.close();
          // --- End of Outdated Scraping Logic ---

          if (allTimes.isEmpty()) {
              System.out.println("\nNo showtimes were successfully scraped. The Fandango website structure has likely changed, or the parsing logic failed.");
              secondPhase = false; // Prevent combination calculation
          }

      } catch (FileNotFoundException e) { // Catch block for Fandango connection try
          System.out.println("Error: Could not find or access the Fandango URL: " + finalLink);
          System.out.println("Please check the link and your internet connection.");
          secondPhase = false;
      } catch (IOException e) { // Catch block for Fandango connection try
          System.out.println("An IO error occurred while trying to fetch showtimes: " + e.getMessage());
          secondPhase = false;
      } catch (Exception e) { // Catch broader exceptions during scraping attempt
          System.out.println("An unexpected error occurred during showtime fetching/parsing: " + e.getMessage());
          e.printStackTrace(); // Print stack trace for debugging
          secondPhase = false;
      } finally { // Finally block for Fandango connection try
           if (inner != null) inner.close();
           if (stream2 != null) try { stream2.close(); } catch (IOException e) { /* ignore */ }
      } // END OF TRY-CATCH-FINALLY for Fandango scraping

    } // End if (secondPhase) for Fandango scraping

    // --- Phase 3: Calculate and Display Double/Triple Features ---
    if (secondPhase && !allTimes.isEmpty()) { // Only proceed if scraping yielded results
      System.out.println("\nWhen do you want to start your first movie? (e.g., 7:30pm or 19:30)");
      timeFrame = in.next(); // Get desired start time input
      double movieTime = 0;
      String timeFrame2 = "";

      try {
          movieTime = minutesToPercent2(timeFrame); // Convert input time to decimal
          timeFrame2 = backToString(movieTime); // Convert back for display (e.g., "7:30 pm")
      } catch (Exception e) {
          System.out.println("Could not parse the time entered. Please use hh:mm[am/pm] format (e.g., 7:30pm or 19:30).");
          // Exit or default? For now, let's exit this part.
          in.close(); // Close scanner before exiting
          return; // Exit main method
      }

      int counter = 0; // Counter for valid first movies found
      Boolean moviesFound = false; // Flag if any combinations are found

      System.out.println("\n--- Potential Double/Triple Features ---");
      // Use chosenTheatre2 here, which should now be in scope
      System.out.println("Showing combinations for '" + chosenTheatre2 + "' on " + dateToUse + " starting around " + timeFrame2 + ":");

      // Loop through all found showtimes to find potential first movies
      for (int x = 0; x < allTimes.size(); x++){
        double firstStartTime = filmStartTime.get(x); // Start time of movie x (decimal)

        // Check if the movie starts at or after the desired time, and within a reasonable window (e.g., 1.5 hours)
        if ((firstStartTime >= movieTime) && (firstStartTime < movieTime + 1.5)){ // Check within 1.5hr window after desired start
          double firstEndTime = filmStartTime.get(x) + filmDuration.get(x); // Calculate end time
          String firstEndString = backToString(firstEndTime); // Format end time
          // Print the first movie option
          System.out.println("\nOption " + (counter + 1) + ":");
          System.out.println("  1st: " + justMovies.get(x) + " [" + filmDurationString.get(x) + "]");
          System.out.println("       Start: " + justTime.get(x) + " | End: " + firstEndString);
          counter++; // Increment first movie counter
          moviesFound = true; // Found at least one starting point
          int counter2 = 0; // Counter for second movies for this first movie

          // Loop through all showtimes again to find potential second movies
          for (int y = 0; y < allTimes.size(); y++){
            // Skip if it's the same exact showtime as the first movie
            if (x == y && filmStartTime.get(x).equals(filmStartTime.get(y))) continue;

            double secondStartTime = filmStartTime.get(y); // Start time of movie y

            // Check if the second movie starts after the first one ends (allowing some gap/overlap)
            double gapStart = firstEndTime - (10.0/60.0); // Start check 10 mins before first ends
            double gapEnd = firstEndTime + (45.0/60.0); // End check 45 mins after first ends

            if (secondStartTime >= gapStart && secondStartTime <= gapEnd){
              double secondEndTime = filmStartTime.get(y) + filmDuration.get(y); // Calculate end time
              String secondEndTime2 = backToString(secondEndTime); // Format end time
              // Print the second movie option
              System.out.println("    -> 2nd: " + justMovies.get(y) + " [" + filmDurationString.get(y) + "]");
              System.out.println("           Start: " + justTime.get(y) + " | End: " + secondEndTime2);
              counter2++; // Increment second movie counter
              int counter3 = 0; // Counter for third movies

              // Loop through all showtimes again for potential third movies
              for (int z = 0; z < allTimes.size(); z++){
                 // Skip if it's the same exact showtime as the first or second movie
                 if ((x == z && filmStartTime.get(x).equals(filmStartTime.get(z))) || (y == z && filmStartTime.get(y).equals(filmStartTime.get(z)))) continue;

                double thirdStartTime = filmStartTime.get(z); // Start time of movie z

                // Check if the third movie starts within the gap after the second one ends
                double gapStart2 = secondEndTime - (10.0/60.0); // Start check 10 mins before second ends
                double gapEnd2 = secondEndTime + (45.0/60.0); // End check 45 mins after second ends

                if (thirdStartTime >= gapStart2 && thirdStartTime <= gapEnd2){
                  counter3++; // Increment third movie counter
                  double thirdEndTime = filmStartTime.get(z) + filmDuration.get(z); // Calculate end time
                  String thirdEndTime2 = backToString(thirdEndTime); // Format end time
                  // Print the third movie option
                  System.out.println("      -> 3rd: " + justMovies.get(z) + " [" + filmDurationString.get(z) + "]");
                  System.out.println("             Start: " + justTime.get(z) + " | End: " + thirdEndTime2);
                } // End if third movie fits
              } // End loop for third movie (z)
            } // End if second movie fits
          } // End loop for second movie (y)
        } // End if first movie fits time window
      } // End loop for first movie (x)

      // If no combinations were found starting around the requested time
      if(!moviesFound){
        System.out.println("\n    No movies found starting around " + timeFrame2 + ".");
        System.out.println("    Try an earlier or later time, or check the scraped showtimes list (if any were found).");
      }
    } else if (secondPhase && allTimes.isEmpty()) {
        // Message if scraping was attempted but failed to get data
        System.out.println("\nCannot calculate combinations because no showtimes were successfully scraped.");
    } else {
        // Message if Fandango link wasn't found earlier or other error occurred
        System.out.println("\nCannot calculate combinations because the Fandango link was not found or an earlier error occurred.");
    }

    in.close(); // Close the main input scanner at the end
    System.out.println("\nProgram finished.");

  } // End main method

  // --- Helper Methods for Time Conversion ---

  // Converts time string (e.g., "7:30pm") to decimal hours (e.g., 19.5)
  private static double minutesToPercent2(String timeStr) throws NumberFormatException, StringIndexOutOfBoundsException {
      timeStr = timeStr.toLowerCase().trim();
      String timeHour;
      String timeMinutes;
      boolean isPM = timeStr.contains("pm");
      // Handle potential lack of am/pm by checking hour (simplistic)
      boolean isAM = timeStr.contains("am");
      timeStr = timeStr.replaceAll("[^\\d:]", ""); // Remove am/pm and any other non-digit/colon chars

      if (!timeStr.contains(":")) {
           // Handle cases like "7" -> assume "7:00"
           if (timeStr.length() > 0 && timeStr.length() <= 2) {
               timeHour = timeStr;
               timeMinutes = "00";
           } else {
               throw new NumberFormatException("Invalid time format (no colon): " + timeStr);
           }
      } else {
          timeHour = timeStr.substring(0, timeStr.indexOf(":"));
          timeMinutes = timeStr.substring(timeStr.indexOf(":") + 1);
          // Ensure minutes are two digits (handle cases like "7:3")
          if (timeMinutes.length() == 1) timeMinutes = "0" + timeMinutes;
          if (timeMinutes.length() != 2) throw new NumberFormatException("Invalid minutes format: " + timeMinutes);
      }

      double hours = Double.parseDouble(timeHour);
      double minutes = Double.parseDouble(timeMinutes);

      // Basic AM/PM logic if present
      if (isPM && hours != 12) hours += 12;
      if (isAM && hours == 12) hours = 0; // 12 AM is 0 hours
      // If no AM/PM, assume 24-hour if hour > 12, otherwise assume PM if hour >= 7? (Very ambiguous)
      // Best to require am/pm or 24hr format for clarity. This method needs improvement.

      if (hours < 0 || hours >= 24 || minutes < 0 || minutes >= 60) {
          throw new NumberFormatException("Invalid hour or minute value.");
      }

      minutes = minutes / 60.0; // Use 60.0 for double division
      minutes = Math.round(minutes * 1000.0) / 1000.0; // Round to 3 decimal places

      return hours + minutes;
  }


  // Converts decimal hours (e.g., 19.5) back to a formatted time string (e.g., "7:30 pm")
  private static String backToString(double decimalTime) {
      // Handle times crossing midnight (e.g., movie ends at 25.5 -> 1:30 am next day)
      String dayIndicator = "";
      // Normalize time to be within 0-24 range for calculation
      while (decimalTime >= 24) {
          decimalTime -= 24;
          dayIndicator = " (next day)"; // Simple indicator, doesn't handle multiple days
      }
      while (decimalTime < 0) { // Handle potential negative results from subtractions
          decimalTime += 24;
          dayIndicator = " (prev day?)"; // Indicate potential past time
      }

      int totalMinutes = (int) Math.round(decimalTime * 60);
      int hours24 = totalMinutes / 60;
      int minutes = totalMinutes % 60;

      // Handle edge case where rounding pushes minutes to 60
      if (minutes == 60) {
          hours24 += 1;
          minutes = 0;
          if (hours24 == 24) { // Rolled over to next day midnight
              hours24 = 0;
              dayIndicator = " (next day)";
          }
      }

      int hours12 = hours24 % 12;
      if (hours12 == 0) { // 0 and 12 map to 12 o'clock
          hours12 = 12;
      }

      String ampm = (hours24 < 12) ? "am" : "pm"; // Midnight (0) is AM, Noon (12) is PM

      // Format the string
      return String.format("%d:%02d %s%s", hours12, minutes, ampm, dayIndicator);
  }

} // End class Double_Feature
