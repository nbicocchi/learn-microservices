echo $'adding student named "Aldo Tillo Poglis"'
curl -X POST http://localhost:7776/ -H "Content-Type: application/json" -d '{"name": "Aldo Tillo", "surname": "Poglis"}'
echo $'\n'
echo $'getting all students'
curl -X GET http://localhost:7776/
echo $'\n'
echo "adding grade for student with id = 1"
curl -X POST http://localhost:7777/1 -H "Content-Type: application/json" -d '{"grade": 10}'
echo $'\n'
echo "getting grades for student with id = 1"
curl -X GET http://localhost:7777/1
echo $'\n'
echo "getting all grades"
curl -X GET http://localhost:7777/