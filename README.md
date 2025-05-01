# Java Movie Double Feature Finder (Circa ~2014-2017)

## Project Overview

This is a Java command-line application developed around 2014-2017. Its original purpose was to help users find potential movie "double features" (or even triple features) playing at local movie theaters.

It worked by:
1.  Asking the user for their location (Zip Code or City).
2.  Scraping Bing search results to find nearby movie theaters.
3.  Asking the user to select a theater.
4.  Scraping Bing search results again to find a Fandango link for the chosen theater.
5.  Scraping the Fandango theater page for movie titles, durations, and showtimes for a specific date.
6.  Asking the user for their desired start time for the first movie.
7.  Calculating and displaying possible sequences of 2 or 3 movies where the next movie started within a reasonable timeframe after the previous one ended.

## Current Status & Limitations

**IMPORTANT:** This code relies heavily on web scraping specific HTML structures from Bing and Fandango as they existed many years ago. Due to frequent website redesigns, the scraping portions of this code are **almost certainly non-functional today** and would require a complete rewrite using modern scraping techniques (like libraries such as Jsoup for HTML parsing or Selenium for JavaScript-heavy sites) and updated website structure analysis.

This repository serves primarily as an archive of the original project and demonstrates the logic used for calculating the movie combinations.

## Original Dependencies (Likely)

* Java Development Kit (JDK) - Version from that era (e.g., JDK 7 or 8).

## How to Compile (Theoretically)

If you have a compatible JDK installed, you could try compiling from the command line within the project directory:

```bash
javac Double_Feature.java