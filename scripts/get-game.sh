GAME_ID=64fd21e6-3c83-43a4-86db-1c64cc2888a4

curl -i "localhost:8080/games/$GAME_ID"\
	-H 'Content-Type: application/json' -H 'Accept: application/json'
