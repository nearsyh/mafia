GAME_ID=ecf9aeba-5b52-4a06-99a6-27f7311455c4

curl -i "localhost:8080/games/$GAME_ID"\
	-H 'Content-Type: application/json' -H 'Accept: application/json'
