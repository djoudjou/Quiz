


Test de création d'un utilisateur

curl -H "Content-Type: application/json" -d '{ "firstname" : "aurélien", "lastname" : "djoutsop", "mail" : "a.djoutsop@gmail.com","password" : "DjouDjou"}' http://localhost:9000/api/user


MAC /Applications/Couchbase\ Server.app/Contents/Resources/couchbase-core/bin/

couchbase-cli bucket-create -c localhost:8091 --bucket=quiz --bucket-type=couchbase --bucket-port=11222 --bucket-ramsize=100 --bucket-replica=1 --enable-flush=1 -u $$ -p $$
couchbase-cli bucket-flush -c localhost:8091 --force --bucket=quiz  -u $$ -p $$


lancer un actor backend :
    activator "runMain StartBackend 2551"
    activator "runMain StartBackend 2552"
	
	activator "runMain StartPlayerWorker 3000"
	activator "runMain StartPlayerWorker 3001"

