# README
[![CI](https://github.com/enricogoerlitz/StudyBlogBackend/actions/workflows/ci.yml/badge.svg)](https://github.com/enricogoerlitz/StudyBlogBackend/actions/workflows/ci.yml)

### How to use (for the profs)
Unsere App finden Sie unter diesem Link: <a href="https://studyblog-df.herokuapp.com/login">StudyBlog</a> <br>
***(Sowohl das FE, als auch das BE benötigen etwas Zeit um zu starten. Wenn Sie eine Login-Form sehen, ist die Anwendung fertig geladen)*** <br>

Wir haben Ihnen sowohl einen Admin-User (Benutzername: prof, Passwort: profpw), <br>
als auch einen Student-User (Benutzername: profasstudent, Passwort: profasstudentpw) angelegt. Sie können sich auf der Login-Page oder auch auf der Register-Page ebenfalls als Visitor-User anmelden <br>
Die Anwendung funktioniert wie in der README beschrieben. <br>
#### Allgemein
- Überall sind validierungen sowohl client-, als auch serverseitig hinterlegt

#### Login-Page
- kann sich anmelden
- kann sich als Visitor anmelden
- kann zu der Register-Page navigieren

#### Register-Page
- kann sich registrieren (inital mit rolle "student")
- kann sich als Visitor anmelden
- kann zu der Login-Page navigieren

#### BlogPost-Page
##### Admin
- kann BlogPosts löschen (abgesehen von anderen Admin-User)
- kann alle BlogPosts bearbeiten (alle)
- kann alles was Student kann

##### Student
- kann BlogPosts filtern (Volltextsuche in [Titel, Text, Erstellername])
- kann BlogPosts filtern nach Favoriten
- kann BlogPosts favorisieren / entfavorisieren
- kann seine eigenen BlogPosts bearbeiten
- kann BlogPost hinzufügen
-
##### Visitor
- kann nur BlogPosts anschauen und Volltextfiltern

### Profile
- nur möglich für Admins und Studenten
- kann Benutzernamen ändern
- kann Passwort ändern (lässt man das PW-Feld leer, wird das PW nicht überschrieben; wenn nicht leer, muss Passwort durch Validierung)

### User Management
- nur Zugriff für Admins
- kann nach User filtern (Id, Username, Role)
- kann Studenten bearbeiten
- kann Studenten löschen
- kann neuen User hinzufügen (inkl. Rollenvergabe)

<br />

### Description
StudyBlog is an application for student to share there students life. <br />
<br />


#### Visitor-Role
You just want to take a look into this application? Cool! You can sign in as a visitor and take a look to the posts.

<br />

#### Student-Role
As a Student, you can create, edit or delete a BlogPost on/at a public page. <br />
You can also favor posts you like. These post will be pushed up at the published date and marked as favourite. <br />
At least, you can find posts by a full-text search and your favourites by button-click in the filter area.

<br/>


#### Admin-Role
Admin's have the honor to manage the users of the application. <br/>
They can add, edit, and delete users with the student-role in the User-Management-Area. <br />
Also, they can edit, or delete posts of students, if they publish offensive stuff.

<br />
<br />

### Used Ressources
JWT & Auth:
<br />
https://www.youtube.com/watch?v=VVn9OG9nfH0
https://www.youtube.com/watch?v=DrKA56M1NRs
<br />
<br />
General:
<br />
https://www.youtube.com/channel/UCwbZGqV2XymPKur91p1O9Ig
<br />
https://stackoverflow.com/

