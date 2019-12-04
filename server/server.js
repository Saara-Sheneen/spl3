const express = require('express')
const app = express()
const port = 3000
var path = require("path");
const exec = require('child_process').exec;
var bodyParser = require('body-parser')

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

var command = path.join(__dirname, '/../run.sh')


app.post('/', (request, response) => {
    
    exec('python trial.py ' + request.body.ip + ' ' + request.body.type,
        (error, stdout, stderr) => {
            console.log(stdout);
            console.log(stderr);
            if (error !== null) {
                console.log(`exec error: ${error}`);
            }
        });
    response.send(request.body.ip)
})

app.listen(port, (err) => {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})
