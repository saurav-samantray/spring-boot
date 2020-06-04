# corenlp-springboot

## Build
`
mvn clean install
`

## Docker Command
`
docker build -t corenlp-app:v1 .
`

`
docker run -p 8080:8080 corenlp-app:v1
`

## NER cURL
`
curl --location --request POST 'localhost:8080/nlp/ner' \
--header 'Content-Type: application/json' \
--data-raw '{
	"text":"ORS Olive Oil Hues Vitamin & Oil Cocoa Brown with instruction sheet 200 oz for under $300 for a meeting in Dubai tomorrow"
}'
`

## Sentiment Analysis curl
`
curl --location --request POST 'localhost:8080/nlp/sentiment' \
--header 'Content-Type: application/json' \
--data-raw '{
	"text":"Im so sad and unhappy."
}'
`