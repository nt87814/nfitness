Social Fitness - README
===

# Social Fitness

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app for users to challenge their friends by creating workouts with deadlines  

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social/Productivity
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
   * Workout detail
* Search
   * Workout detail
   * Other user profile
* Creation Screen
   * Home (after you finish posting the photo)


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://i.imgur.com/5MKgLSz.jpg"
 width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models

#### Post (workout)

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| user who created the workout |
   | type        | pointer to a category | workout can be endurance, strength, flexibility |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | description   | String   | workout description |
   | time       | Number   | time of workout (for running, biking)|
   | difficulty | Number   | difficulty rating based on user average |
   | likes    | array of pointers to users   | likes for the post |
   | created_at     | DateTime | date when post is created (default field) |
   
   #### Challenge
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |
   | owner_id      | Pointer to User| user who sent this challenge |
   | workout_id    | pointer to workout | reference to workout |
   | recipient_id  | Pointer to user| the user the author sent this to |
   | deadline      | DateTime | date when post expires |
   | completed     | DateTime | Shows whether the user has completed the challenge in time |
   | time      | Number | time for endurance workout |

   
   
   #### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | id      | String   | unique id for the user post (default field) |  
   | first_name      | String   | User's first name |  
   | last_name      | String   | User's last name |
   | password      | String   | User's password |
   | image      | File | Profile picture |
   | created_at     | DateTime | date when user is created (default field) |
   | updated_at     | DateTime | date when user is last updated (default field) |
   | friends      | Array of users   | List of friends |
   
   #### Category
   
  | Property      | Type     | Description |
  | ------------- | -------- | ------------|
  | id      | String   | unique id for the category |  
  | Name      | String   | Name describes the category | 
   
### Networking

   - Home Feed Screen
      - (Read/GET) Query all challenges where not completed and recipient is current user
      - (Delete) Delete existing like
   - Search/Filter Screen
       - (Read/GET) Query all workouts where type is ... 
   - Post Detail Screen
      - (Read/GET) challenge
      - (Create/POST) start challenge
      - (Create/POST) complete challenge (if started)
      - (Create/POST) challenge a friend
   - Create Post Screen
      - (Create/POST) Create a new workout
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Read/GET) Query all challenges where completed and recipient is current user
      - (Read/GET) Query users that are friends with current user
         ```swift
         let query = PFQuery(className:"Post")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Update/PUT) Update user profile image

- [OPTIONAL: List endpoints if using existing API such as Yelp]