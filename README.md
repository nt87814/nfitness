Activley - README
===

# Activley

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Activley is an app that lets users add workouts to their challenge list with deadlines and challenge their friends to complete workouts. The app shows a user's stats based on the workouts they have done and allows users to create their own workouts.

### App Evaluation

- **Category:** Social/Fitness
- **Mobile:** Push notifications/alerts
- **Story:** Users will have motivation to workout by being challenged by friends and having deadlines
- **Market:** For anyone into fitness
- **Habit:** Users will frequently check for challenge deadlines
- **Scope:** 

## Product Spec

### 1. User Stories (Required and Optional)
(high level)
**Required Must-have Stories**

* User can log in
* User can create an account
* User can view challenge list
* User can create a new workout
* User can start challenges
* User can complete challenges
* User can challenge a friend
* User can view profile page
* User can view workout detail page
* User can time a workout

**Optional Nice-to-have Stories**
* User can search for workouts
### 2. Screen Archetypes

* Login screen
   * User can login
* Registration screen
   * User can create a new account
 * Workout Stream
   * User can view a stream of challenges
 * Workout Detail 
   * Displays details not in the stream
 * Workout creation
   * User can post a new workout
   * User can upload a description
   * User can upload photos
 * Search
   * User can search by type
 * Profile
   * User can view information about their own account
   * User can view other user's profiles
 * Settings
   * User can edit their account

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed
* Post a workout 
* Search
* Profile detail

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Home
* Registration Screen
   * Home
* Home feed
   * Post detail
   * Friends' profiles
* Explore
   * Add workout
* Map
   * Add workout  
* Creation Screen
   * Post detail (after you finish posting the photo)
* Profile
   * Post detail 


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://i.imgur.com/5MKgLSz.jpg"
 width=600>

## Schema 
### Models

#### Post (workout)

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| user who created the workout |
   | type        | String| workout can be endurance, strength, flexibility |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | description   | String   | workout description |
   | time       | Number   | time of workout (for running, biking)|
   | difficulty | Number   | difficulty rating based on user average |
   | likes    | array of pointers to users   | likes for the post |
   | created_at     | DateTime | date when post is created (default field) |rjfedulhrbuvktbjjnhlivnvlkbjekkfgegiiurnutkthtkeuigcchkdebbundrgthdifccbblcdlgklrjihcvukucijbrec
   
   #### Challenge
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |
   | owner_id      | Pointer to User| user who sent this challenge |
   | workout_id    | pointer to workout | reference to workout |
   | recipient_id  | Pointer to user| the user the author sent this to |
   | started       | DateTime | date when workout was added |
   | deadline      | DateTime | date when post expires |
   | completed     | DateTime | Shows whether the user has completed the challenge in time |

   
   
   #### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |  
   | first_name      | String   | User's first name |  
   | last_name      | String   | User's last name |
   | password      | String   | User's password |
   | created_at     | DateTime | date when post is created (default field) |
   | updated_at     | DateTime | date when post is last updated (default field) |
   | friends      | Array of user ids   | List of friends |
   
   
### Networking

   - Home Feed Screen
      - (Read/GET) Query all challenges where not completed and recipient is current user
      - (Delete) Delete existing like
   - Explore Screen
       - (Read/GET) Query all workouts by category
   - Post Detail Screen
      - (Read/GET) challenge
      - (Create/POST) complete challenge (if started)
      - (Create/POST) challenge a friend
   - Create Post Screen
      - (Create/POST) Create a new workout
   - Profile Screen
      - (Read/GET) Query all challenges where completed and    recipient is current user
      - (Read/GET) Query users that are friends with current user
      - (Update/PUT) Update user profile image

* Your app has multiple views
  * Four main views from bottom navigation and a create screen 
* Your app interacts with a database (e.g. Parse) 
  * Parse platform 
* You can log in/log out of your app as a user 
  * You can log in/log out
* You can sign up with a new user profile 
  * You can register as a new user
* Somewhere in your app you can use the camera to take a picture and do something with the picture (e.g. take a photo and share it to a feed, or take a photo and set a user’s profile picture)
  * You can use the camera to change your profile photo 
* Your app integrates with a SDK (e.g. Google Maps SDK, Facebook SDK)
  * Google Maps SDK 
* Your app contains at least one more complex algorithm (talk over this with your manager) 
  * Algorithm for determining a user's top category 
* Your app uses gesture recognizers (e.g. double tap to like, e.g. pinch to scale) 
  * swipe to complete or delete 
* Your app use an animation (doesn’t have to be fancy) (e.g. fade in/out, e.g. animating a view growing and shrinking)
  * fade in/fade out between fragments  
* Your app incorporates an external library to add visual polish
  * Material 