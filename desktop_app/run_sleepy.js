var exec = require("child_process").exec;
var path = require("path");

var filePaths = {
  mainFile: path.join(__dirname, "/sleepiness_detection.py "),
  shapePredictorFile: path.join(
    __dirname,
    "shape_predictor_68_face_landmarks.dat "
  ),
  alarmFile: path.join(__dirname, "alarm.wav ")
};

var command =
  "python " +
  filePaths.mainFile +
  "--shape-predictor " +
  filePaths.shapePredictorFile +
  "--alarm " +
  filePaths.alarmFile;

function myFunction() {
  exec(command, (error, stdout, stderr) => {
    console.log(stdout);
    console.log(stderr);
    if (error !== null) {
      console.log(`exec error: ${error}`);
    }
  });
}

//onInputHandler();