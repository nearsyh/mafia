GAME_ID=458de60b-41c0-4da0-b39b-7a99b97745cd

curl -i "localhost:8080/games/$GAME_ID"\
	-H 'Content-Type: application/json' -H 'Accept: application/json'
