GAME_ID=f594b761-5564-4172-a13b-781a1b0b424e

curl -i "localhost:8080/games/$GAME_ID"\
	-H 'Content-Type: application/json' -H 'Accept: application/json'
