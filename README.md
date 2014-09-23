


Test de création d'un utilisateur

curl -H "Content-Type: application/json" -d '{ "firstname" : "aurélien", "lastname" : "djoutsop", "mail" : "a.djoutsop@gmail.com","password" : "DjouDjou"}' http://localhost:9000/api/user