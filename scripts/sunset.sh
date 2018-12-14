GAMEID=772a87d2-9bdb-47c6-9d0e-48b3a4451a21

curl -XPOST "localhost:8080/games/${GAMEID}" -H 'Content-Type: application/json' -H 'Accept: application/json' \
-d '{
	"event_type": "SUNSET"
}'
