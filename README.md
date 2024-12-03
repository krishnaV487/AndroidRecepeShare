# Milestone 1 - Recepe Sharing App (Unit 7)

## Table of Contents

1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)

## Overview
### Description
>This is an android app that allows users to share recipe, they can choose to keep a recepe within a private group or public feed.

### App Evaluation
>User's Dashboard (their recepes and private groups)
>Private group list
>Public Recepes Page
>Add new recepe page
>Log in and out page

[Evaluation of your app across the following attributes]
- **Category:**
- **Mobile:**
> What makes your app more than a glorified website?
>>User can use camera on mobiles to put pictrues in their post
- **Story:**
>How clear is the value of this app to your audience?
>>Crystal clear, This app can be used to share recipes within friends or with everyone

>How well would your friends or peers respond to this product idea?
>>They would preferr an app with ability to share with just friends or everyone
- **Market:**
>What's the size and scale of your potential user base?
>>The world

>Does this app provide huge value to a niche group of people?
>>People can create popular recipes within app

>Do you have a well-defined audience of people for this app?
>>Yes, families who want to keep their recipies secret, and some who want to share it.
- **Habit:**
>How frequently would an average user open and use this app?
>>At least once a day

>Does an average user just consume your app or do they create?
>>Both, mostly logging with analytics
- **Scope:**
>How technically challenging will it be to complete this app by the end of the program?
>> Very, from the start to the end, working with storing images, creating private groups and one public feed etc. will pose challanges.

>Is a stripped-down version of this app still interesting to build?
>>Yes it'll still do the basic function of atleast being able to share recepes better than text messages

>How clearly defined is the product you want to build?
>>There are some clear defined requirements for MVP, stretch features are still abstract
## Product Spec

### 1. User Features (Required and Optional)

**Required Features**

1. MVP:
>**Required Features**:
>>login
>>store recepe
>>make group with other users
>**Stretch Features**
>>public feed
>>enhanced UI
>>search in public feed
2. Fire base Integration
3. API Integration
4. DB usage

2.UX and UI:
>**Navigation**
>>- Tab Navigation: Include at least two primary navigation tabs for easy access to main features.
>>- Flow Navigation: Implement structured navigation flows (e.g., detail screens, screen transitions).

>**Screen Archetypes**
>>- Design with at least three main screen types (e.g., Home, Profile, Detail) to cover the core functions and ensure a clear user journey.

3.Additional Requirements:
>**Mobile Specific Feature**
>>Incorporate at least one mobile-specific capability (e.g., camera, GPS, sensors) that adds unique value to the mobile experience.

>**UI Consistancy**
>>Ensure a consistent look and feel across all screens, with consistent use of fonts, colors, and layouts.

>**link to requirements**
>>Include a link to this markdown file in your project's markdown file (e.g., README.md).


**Optional Features**

1. Public feed
2. Recepe comments, favourate recepes etc.
3. Advanced Search for recepes

### 2. Screen Archetypes

- Daskboard
  - user can see their posted recepes
- Friends and Groups
  - user can see their friends and groups
  - Friends and Groups Detail Page
      - user can now see the shared recepe that is private to this friend and group
- Public Recepes Page
    - user can see all recepes set to public
- Sign in Page
    - user can log in using firebase auth
 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* [dashboard (in tab)]
* [my groups (in tab)]
* [all recepes (in tab)]



**Flow Navigation** (Screen to Screen)

- [sign in]
  - [ from dashboard]
- [new recepe]
  - [ from dashboard]
- [frind/group recepes]
    - [ from my groups]
- [recepe view page]
    - [ from friend/geoup recepe page]
    - [ from all recepe page]
    - [ tap on any listed recepe]


## Wireframes

<img src="https://github.com/user-attachments/assets/9a3b83b0-a146-4480-96da-60c1c374c69d" width=600>
![Screenshot from 2024-11-25 15-41-00])

<br>


# Milestone 2 - Build Sprint 1 (Unit 8)

## GitHub Project board
![Screenshot from 2024-12-02 14-51-48](https://github.com/user-attachments/assets/43220e81-7372-4662-bc0f-5590c566c410)


## Issue cards

- [Add screenshot of your Project Board with the issues that you've been working on for this unit's milestone]
![Screenshot from 2024-12-02 19-32-55](https://github.com/user-attachments/assets/b8d6e2a1-f905-490a-ab2e-596670955334)

- [Add screenshot of your Project Board with the issues that you're working on in the **NEXT sprint**. It should include issues for next unit with assigned owners.]
 ![Screenshot from 2024-12-02 19-32-55](https://github.com/user-attachments/assets/e4f88ed3-c94b-4a01-b1fb-617a28a6f6c1)

**Note**: the completed issues/card are from Milestone 1, while the TODO/Inprogress is from Milestone 2, user auth from Milestone 1 is in progress due to its involvement in Milestone 2.



## Issues worked on this sprint

- List the issues you completed this sprint
  - Navigation between three main Fragments (Dashboard, Feed, Groups)
  - Created three main fragment classes and layouts
  - Create a Room db
  - Create User entity (table)
  - Create User group table
  - Create Recipe table
 Screen shot from github with issue names
  - ![Screenshot from 2024-12-02 19-20-21](https://github.com/user-attachments/assets/0aa4c5ca-748d-4958-8f1b-8726a86f7f9f)

**App In Progress Working:**
[Screencast from 2024-12-02 19-14-08.webm]<img src="https://github.com/user-attachments/assets/9b3ee5d1-778e-466e-80d9-3bb5d811df7f">

CLICK HERE IF VIDEO ABOVE IS NOT WORKING: [Screencast from 2024-12-02 19-14-08.webm](https://github.com/user-attachments/assets/9b11730f-08b6-486f-9485-a487ec22feee)


- [Add giphy that shows current build progress for Milestone 2. Note: We will be looking for progression of work between Milestone 2 and 3. Make sure your giphys are not duplicated and clearly show the change from Sprint 1 to 2.]
- As shown in below screen shots, Milestone 2 has been completed and Milestone 3 will be started. the task for next milestones have been created.

- below screen shots of completed tasks
![Screenshot from 2024-12-02 19-21-20](https://github.com/user-attachments/assets/77eb4307-88b8-4131-afff-2e865be366e8)
- gif showing difference between Sprint 2 and 3
![githubMilestoneUnit8](https://github.com/user-attachments/assets/b6f0cbf3-86d2-45ba-89ab-fb6d9af7d3c1)

<br>

# Milestone 3 - Build Sprint 2 (Unit 9)

## GitHub Project board

[Add screenshot of your Project Board with the updated status of issues for Milestone 3. Note that these should include the updated issues you worked on for this sprint and not be a duplicate of Milestone 2 Project board.] <img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## Completed user stories

- List the completed user stories from this unit
- List any pending user stories / any user stories you decided to cut
from the original requirements

[Add video/gif of your current application that shows build progress]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

## App Demo Video

- Embed the YouTube/Vimeo link of your Completed Demo Day prep video
