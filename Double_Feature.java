import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.lang.*;

public class DoubleFeature3 {

  public static void main (String[] args) throws IOException{
    Scanner in = new Scanner(System.in);
    Boolean searching = true;
    Boolean refresh = true;
    Boolean cityCheck = false;
    Boolean zipCheck = false;
    Boolean movieNum = false;
    Boolean joining = false;
    Boolean secondPhase = true;
    int zipCode = 0;
    int length = 0;
    int choiceNum, optionNum;
    double actualTime2 = 0; //Time of the movie converted to decimal number
    String chosenTheatre = "a";
    String city, timeFrame, actualTime, movies, duration, finalDuration, movieOne, movieOneTime, reply;
    ArrayList<String> allTimes = new ArrayList<String>();
    ArrayList<String> justTime = new ArrayList<String>();
    ArrayList<String> justMovies = new ArrayList<String>();
    ArrayList<String> filmDurationString = new ArrayList<String>();
    ArrayList<String> movieTheatres = new ArrayList<String>();
    ArrayList<Double> filmDuration = new ArrayList<Double>();
    ArrayList<Double> filmStartTime = new ArrayList<Double>();



    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    // System.out.println(timeStamp);
    String year = timeStamp.substring( 2, 4);
    int years = Integer.parseInt(year);
    String month = timeStamp.substring( 4, 6);
    int months = Integer.parseInt(month);
    String day = timeStamp.substring( 6, 8);
    int days = Integer.parseInt(day);
    String date = month+"/"+day+"/"+year;
    System.out.println(date);

    while (searching){
      System.out.println("Zip Code or Name of City");
      reply = in.next();
      refresh = true;
      while (refresh){
        if(reply.matches("^\\d+(\\.\\d+)?")) {
          zipCode = Integer.parseInt(reply);
          length = reply.length();
          zipCheck = true;}
        else{
          city = reply;
          cityCheck = true;}

        if (length == 5 || cityCheck == true){
          URL url1 = new URL("https://www.bing.com/search?q=theatres+near+" + reply);
          URLConnection con1 = url1.openConnection();
          InputStream is1 = con1.getInputStream();
          Scanner insert = new Scanner(new InputStreamReader(is1));
          Boolean reading = true;
          String theatre = "";
          String theatre2 = "";
          String theatre3 = "";
          int count = 0;
          Boolean found2 = false;

          while ((insert.hasNext() && count <= 7)){
            joining = false;
            Boolean found = false;

            if (((insert.next()).equals("/></div></span><p")) || ((insert.next()).equals("class=\"b_vPanel\"><div><p"))){
              theatre = insert.next();
              theatre2 = theatre.substring(12, theatre.length());
              joining = true;
              found = true;
              found2 = true;
              count++;}

            while (joining){
              theatre2 = theatre2 + " " + insert.next();
              if ((theatre2).contains("</p><p")){
                theatre3 = theatre2.substring(0, theatre2.length()-6);
                joining = false;
                if (theatre3.contains("&amp;")){
                  theatre3 = theatre3.replaceAll("&amp;", "&");}
                movieTheatres.add(theatre3);}
            }
            if (found){
              // System.out.println(count+". " + theatre3);}
            }
          }

          if (found2){
            System.out.println("\nThese are the theatres near you - \n");
            for (int m = 0; m < count; m++){
              System.out.println((m+1) + ". " + movieTheatres.get(m));}
            insert.close();


            System.out.println("9. Refresh results");
            System.out.println("0. Try again with another area code");
            System.out.println("\nChoose the corresponding number for the theatre");
            String answer = in.next();
            int answer2 = Integer.parseInt(answer);

            if (answer2 <= 8 && answer2 > 0){
              System.out.println("You chose " + movieTheatres.get(answer2-1));
              chosenTheatre = movieTheatres.get(answer2-1);
              refresh = false;
              searching = false;}
            else if (answer.equals("9")){
              refresh = true;
              searching = true;
              movieTheatres.clear();}
            else if (answer.equals("0")){
              refresh = false;
              searching = true;
              movieTheatres.clear();}
            else {
              System.out.println("You did not chose a valid option, try again!");}
          }

          else{
            System.out.println("No showtimes were found for this theatre, please try again!");}
        }
        else{
          System.out.println("The zip code does not match any records, try again!");}
      }
    }
    String chosenTheatre2 = chosenTheatre;
    chosenTheatre = chosenTheatre.replaceAll("\\s+","+");
    URL urlSearch = new URL("https://www.bing.com/search?q=" + chosenTheatre + "+fandango");
    URLConnection kon = urlSearch.openConnection();
    InputStream stream = kon.getInputStream();

    System.out.println(stream);

    Scanner system = new Scanner(new InputStreamReader(stream));
    Boolean reading2 = false;
    int count = 0;
    String lineOne;
    String link = "";
    String link2 = "";

    while (system.hasNext() && !reading2) {
      lineOne = system.next();
      if (lineOne.contains("theaterpage")){
        link = lineOne.substring(6, lineOne.length()-1);
        link2 = system.next();
        reading2 = true;}
    }
    if (!reading2) {
      System.out.println("We were unable to find showtimes for your theatre!");
      secondPhase = false;}


    System.out.println("\nProvide the date for the showtimes (mm/dd/yy)");
    System.out.println("Today's date is " + date);
    String date2 = in.next();
    int monthTwo = months;
    int dayTwo = days;


    if ((date2.length() == 8)){
      String month2 = date2.substring(0, 2);
      String day2 = date2.substring(3, 5);
      String year2 = date2.substring(6, 8);
      if ((date2.equals(date))||(year2.equals("17")) && (monthTwo > 0) && (monthTwo < 13) && (dayTwo > 0) && (dayTwo < 32)){
        date = date2;
        if (!Character.isDigit(day2.charAt(0))  || !Character.isDigit(day2.charAt(1)) ||
            !Character.isDigit(month2.charAt(0))|| !Character.isDigit(month2.charAt(1)) ||
            !Character.isDigit(year2.charAt(0)) || !Character.isDigit(year2.charAt(1))) {
          System.out.println("Your date was not recognized");}
        else{
          monthTwo = Integer.parseInt(month2);
          dayTwo = Integer.parseInt(day2);}}}
    else{
      System.out.println("The date was not recognized. We will go ahead with today's showtimes");}

    if (secondPhase){
      String finalLink = link + "?date=" + date;
      System.out.println(finalLink);

      //  URL url = new URL(link+"?date="+date);
      //  URLConnection con = url.openConnection();
      //  InputStream is = con.getInputStream();
      //  Scanner inner = new Scanner(new InputStreamReader(is));

      URL urlSearch2 = new URL(finalLink);
      URLConnection kon2 = urlSearch2.openConnection();
      InputStream stream2 = kon2.getInputStream();
      Scanner inner = new Scanner(new InputStreamReader(stream2));
      System.out.println(stream2);

      try{
        String nextLine = inner.nextLine();

        Boolean reading = true;
        Boolean found = true;
        Boolean readingShow;
        Boolean possibilities = true;
        String line;
        String line2;
        String theatre;

        while ((inner.hasNextLine())){ //&& //reading) {

          System.out.println("hi");


          // if ((inner.nextLine()).contains("itemtype=\"http://schema.org/TheaterEvent\"")){
          //for (int a = 0; a < filmDurationString.size() ; a++){
          //String newbie = filmDurationString.get(a);
          //System.out.println(newbie);}
          if ((inner.nextLine()).contains("itemtype=\"1\"")){
            line = inner.nextLine();
            line = line.trim();
            movies = line.substring(31, line.length()-2);
            line2 = inner.nextLine();
            line2 = line2.trim();
            System.out.println("A"+movies);

            if ((line2.substring(line2.length()-7, line2.length()-6)).equalsIgnoreCase("T")){
              duration = line2.substring(line2.length()-6, line2.length()-2);}
            else{
              duration = line2.substring(line2.length()-7, line2.length()-2);}

            duration = duration.replaceAll("H", ".");
            duration = duration.replaceAll("M", "");
            String decimal = duration.substring(duration.length()-2, duration.length()-1);
            if (decimal.equals(".")){
              duration = duration.substring(0, 2) + "0" + duration.substring(2, duration.length());}

            String durationHour = duration.substring(0, 1);
            String durationMinutes = duration.substring(2, 4);
            finalDuration = durationHour + "h " + durationMinutes + "m";

            double minutes = Double.parseDouble(durationMinutes);
            minutes = minutes/60;
            minutes = Math.round(minutes*1000.0)/1000.0;
            double hours = Double.parseDouble(durationHour);
            double finalTime = hours + minutes;
            // int length2 = String.valueOf(finalTime).length();

            if (movies.contains(": An IMAX 3D Experience")){
              movies = movies.replaceAll(": An IMAX 3D Experience", " (IMAX)");}
            if ((movies.contains("2017")||movies.contains("3D")) || (movies.contains("2017")&&movies.contains("3D"))){
              movies = movies.replaceAll("2017", "");
              movies = movies.replaceAll("[()]", " ");
              movies = movies.replaceAll("3D", "(3D)");}
            movies = movies.replaceAll("\\s+$", "");
            movies = movies.replaceAll("“", "\"");
            movies = movies.replaceAll("”", "\"");

            // if (movies.contains("3D")){
            //  movies = movies.replaceAll("3D", "(3D)");}
            if (found){
              found = false;}
            readingShow = true;

            while (readingShow && inner.hasNextLine()){
              String showtime = inner.nextLine();
              showtime = showtime.trim();

              if (showtime.length() > 3){
                actualTime = showtime.substring(47, showtime.length()-11);
                actualTime2 = minutesToPercent2 (actualTime); // Movie time converted to percent
                String allShows = "[" + finalTime + "] " + movies + " - " + actualTime; // Combines all info to format - MovieName - timing1 timing2 etc
                allTimes.add(allShows);
                justMovies.add(movies);
                justTime.add(actualTime);
                filmDuration.add(finalTime);
                filmDurationString.add(finalDuration);
                filmStartTime.add(actualTime2);}
              if (inner.nextLine().contains("</span>")){
                readingShow = false;}
            }
          }
          System.out.println("abc");
        }
      }
      catch(NoSuchElementException ex)
      {
        System.out.print("not working");
      }


      System.out.println("When do you want to start your first movie?");
      timeFrame = in.next();
      double movieTime = minutesToPercent (timeFrame);
      String timeFrame2 = backToString (movieTime); // Movie start time to proper time format w/ pm or am
      int counter = 0;
      Boolean moviesFound = false;

      System.out.println("Here are the showtimes for '" + chosenTheatre2 + "' movies for " + date + " that start after " + timeFrame2);
      for (int x = 0; x < allTimes.size(); x++){
        double firstStartTime = filmStartTime.get(x);
        System.out.println(firstStartTime);
        int counter2 = 0;

        timeFrame = timeFrame.substring(timeFrame.length()-5,timeFrame.length()-3) + "." + timeFrame.substring(timeFrame.length()-2,timeFrame.length());
        double finalTimeFrame = Double.parseDouble(timeFrame);

        if ((firstStartTime >= finalTimeFrame) && ((finalTimeFrame+1) > firstStartTime)){  // the starting time is between a wanted time and an hour past the wanted time
          double firstEndTime = filmStartTime.get(x) + filmDuration.get(x);
          String firstEndString = backToString2 (firstEndTime);
          System.out.println("\n"+(counter+1)+". "+justMovies.get(x)+" ["+filmDurationString.get(x)+"]"+"\n   Start Time: "+justTime.get(x)+" || End Time: "+firstEndString);
          counter++;
          moviesFound = true;

          for (int y = 0; y < allTimes.size(); y++){
            double secondStartTime = filmStartTime.get(y); // The starting hour of the movie
            double firstEndTimeZ = firstEndTime + 0.75;
            int counter3 = 0;

            if ((secondStartTime >= (firstEndTime-0.10)) && ((firstEndTimeZ) >= secondStartTime)){  // the starting time is between a wanted time and an hour past the wanted time
              double secondEndTime = filmStartTime.get(y) + filmDuration.get(y);
              String secondEndTime2 = backToString3 (secondEndTime);
              System.out.println("     "+(counter2+1)+") "+justMovies.get(y)+" ["+filmDurationString.get(y)+"]"+"\n        Start Time: "+justTime.get(y)+" || End Time: "+secondEndTime2);
              counter2++;

              for (int z = 0; z < allTimes.size(); z++){
                double thirdStartTime = filmStartTime.get(z); // The starting hour of the movie
                double secondEndTimeZ = secondEndTime + 0.75;

                if ((thirdStartTime >= (secondEndTime-0.10)) && ((secondEndTimeZ) >= thirdStartTime)){  // the starting time is between a wanted time and an hour past the wanted time
                  counter3++;
                  double thirdEndTime = filmStartTime.get(z) + filmDuration.get(z);
                  String thirdEndTime2 = backToString4 (thirdEndTime);
                  System.out.println("          "+(counter3)+") "+justMovies.get(z)+" ["+filmDurationString.get(z)+"]"+"\n             Start Time: "+justTime.get(z)+" || End Time: "+thirdEndTime2);
                }
              }
            }
          }
        }
      }
      if(!moviesFound){
        System.out.print("    There are no movies around your requested time!");}
    }
  }

  private static double minutesToPercent (String timeFrame){ // Method that converts time to a percentage
    String timeMinutes = timeFrame.substring(3, 5);
    String timeHour = timeFrame.substring(0, 2);
    double minutes = Double.parseDouble(timeMinutes);
    minutes = minutes / 60;
    minutes = Math.round(minutes*1000.0)/1000.0;
    double hours = Double.parseDouble(timeHour);
    double finalTime = hours + minutes;
    return finalTime;}

  private static double minutesToPercent2 (String actualTime){ // Method that converts time to a percentage
    String timeMinutes = actualTime.substring(3, 5);
    String timeHour = actualTime.substring(0, 2);
    double minutes = Double.parseDouble(timeMinutes);
    minutes = minutes / 60;
    minutes = Math.round(minutes*1000.0)/1000.0;
    double hours = Double.parseDouble(timeHour);
    double finalTime = hours + minutes;
    return finalTime;}

  private static double minutesToPercent3 (String secondStartTime){ // Method that converts time to a percentage
    String timeMinutes = secondStartTime.substring(3, 5);
    String timeHour = secondStartTime.substring(0, 2);
    double minutes = Double.parseDouble(timeMinutes);
    minutes = minutes / 60;
    minutes = Math.round(minutes*1000.0)/1000.0;
    double hours = Double.parseDouble(timeHour);
    double finalTime = hours + minutes;
    return finalTime;}

  private static String backToString (double movieTime){ // Method that calculates the average temperature for the month
    double minutes = (int) movieTime;
    minutes = (movieTime - minutes) * 60;
    int minute = (int) Math.round(minutes);
    double hours = (int) movieTime;
    int hour = (int) hours;
    if (hour >= 24){
      hour = hour - 24;}
    String time = null;
    if (minute == 0){
      time = Integer.toString(hour) + ":00";}
    else if (minute < 10){
      time = Integer.toString(hour) + ":0" + Integer.toString(minute);}
    else{
      time = Integer.toString(hour) + ":" + Integer.toString(minute);}
    if (hours < 12){
      time = time + " am";}
    else if (hours >= 12){
      time = time + " pm";}
    return time;}

  private static String backToString2 (double firstEndTime){ // Method that calculates the average temperature for the month
    double minutes = (int) firstEndTime;
    minutes = (firstEndTime - minutes) * 60;
    int minute = (int) Math.round(minutes);
    double hours = (int) firstEndTime;
    int hour = (int) hours;
    String hourString;
    if (hour >= 24){
      hour = hour - 24;
      hourString = "0" + Integer.toString(hour);}
    else{
      hourString = Integer.toString(hour);}
    String time = null;
    if (minute == 0){
      time = hourString + ":00";}
    else if (minute < 10){
      time = hourString + ":0" + Integer.toString(minute);}
    else{
      time = hourString + ":" + Integer.toString(minute);}
    return time;}


  private static String backToString3 (double secondEndTime){ // Method that calculates the average temperature for the month
    double minutes = (int) secondEndTime;
    minutes = (secondEndTime - minutes) * 60;
    int minute = (int) Math.round(minutes);
    double hours = (int) secondEndTime;
    int hour = (int) hours;
    String hourString;
    if (hour >= 24){
      hour = hour - 24;
      hourString = "0" + Integer.toString(hour);}
    else{
      hourString = Integer.toString(hour);}
    String time = null;
    if (minute == 0){
      time = hourString + ":00";}
    else if (minute < 10){
      time = hourString + ":0" + Integer.toString(minute);}
    else{
      time = hourString + ":" + Integer.toString(minute);}
    return time;}

  private static String backToString4 (double thirdEndTime){ // Method that calculates the average temperature for the month
    double minutes = (int) thirdEndTime;
    minutes = (thirdEndTime - minutes) * 60;
    int minute = (int) Math.round(minutes);
    double hours = (int) thirdEndTime;
    int hour = (int) hours;
    String hourString;
    if (hour > 24){
      hour = hour - 24;
      hourString = "0" + Integer.toString(hour);}
    else{
      hourString = Integer.toString(hour);}
    String time = null;
    if (minute == 0){
      time = hourString + ":00";}
    else if (minute < 10){
      time = hourString + ":0" + Integer.toString(minute);}
    else{
      time = hourString + ":" + Integer.toString(minute);}
    return time;}
}
