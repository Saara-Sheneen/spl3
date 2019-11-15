var path = require("path");
const exec = require('child_process').exec;

console.log(__dirname)

var command = path.join(__dirname, '/run.sh')

var yourscript = exec('sh ' + command,
        (error, stdout, stderr) => {
            console.log(stdout);
            console.log(stderr);
            if (error !== null) {
                console.log(`exec error: ${error}`);
            }
        });