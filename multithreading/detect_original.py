# Based on https://github.com/tensorflow/examples/blob/master/lite/examples/object_detection/raspberry_pi/README.md
import re
import cv2
import time
from tflite_runtime.interpreter import Interpreter
import numpy as np
import ImageCollage

CAMERA_WIDTH = 640
CAMERA_HEIGHT = 480

def load_labels(path='labels.txt'):
  """Loads the labels file. Supports files with or without index numbers."""
  with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
    labels = {}
    for row_number, content in enumerate(lines):
      pair = re.split(r'[:\s]+', content.strip(), maxsplit=1)
      if len(pair) == 2 and pair[0].strip().isdigit():
        labels[int(pair[0])] = pair[1].strip()
      else:
        labels[row_number] = pair[0].strip()
  return labels

def set_input_tensor(interpreter, image):
  """Sets the input tensor."""
  tensor_index = interpreter.get_input_details()[0]['index']
  input_tensor = interpreter.tensor(tensor_index)()[0]
  input_tensor[:, :] = np.expand_dims((image-255)/255, axis=0)


def get_output_tensor(interpreter, index):
  """Returns the output tensor at the given index."""
  output_details = interpreter.get_output_details()[index]
  tensor = np.squeeze(interpreter.get_tensor(output_details['index']))
  return tensor


def detect_objects(interpreter, image, threshold):
  """Returns a list of detection results, each a dictionary of object info."""
  set_input_tensor(interpreter, image)
  interpreter.invoke()
  # Get all output details
  boxes = get_output_tensor(interpreter, 1)
  classes = get_output_tensor(interpreter, 3)
  scores = get_output_tensor(interpreter, 0)
  count = int(get_output_tensor(interpreter, 2))

  results = []
  for i in range(count):
    if scores[i] >= threshold:
      result = {
          'bounding_box': boxes[i],
          'class_id': classes[i], 
          'score': scores[i]
      }
      results.append(result)
  return results

def main():
    labels = load_labels()
    interpreter = Interpreter('detect.tflite')
    interpreter.allocate_tensors()
    _, input_height, input_width, _ = interpreter.get_input_details()[0]['shape']

    cap = cv2.VideoCapture(0)

    fps_start_time=0
    fps=0

    while cap.isOpened():
        ret, frame = cap.read()
        fps_end_time=time.time()
        time_diff=fps_end_time - fps_start_time
        fps=1/(time_diff)
        fps_start_time = fps_end_time

        fps_text = "FPS: {:.2f}".format(fps)
        cv2.putText(frame, fps_text,(5,30), cv2.FONT_HERSHEY_SIMPLEX, 1,(0,255,255),2)
        img = cv2.resize(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB), (320,320))
        res = detect_objects(interpreter, img, 0.75)
        #print(res)
        #if res:
        #    print("printing: "+ str(res))

        for result in res:
            print(result)
            ymin, xmin, ymax, xmax = result['bounding_box']
            xmin = int(max(1,xmin * CAMERA_WIDTH))
            xmax = int(min(CAMERA_WIDTH, xmax * CAMERA_WIDTH))
            ymin = int(max(1, ymin * CAMERA_HEIGHT))
            ymax = int(min(CAMERA_HEIGHT, ymax * CAMERA_HEIGHT))
            
            cv2.rectangle(frame,(xmin, ymin),(xmax, ymax),(0,255,0),3)
            cv2.putText(frame,labels[int(result['class_id'])],(xmin, min(ymax, CAMERA_HEIGHT-20)), cv2.FONT_HERSHEY_SIMPLEX, 0.5,(255,255,255),2,cv2.LINE_AA) 
            #filename="../capturedImage/"+labels[int(result['class_id'])]+".jpg"
            #cv2.imwrite(filename, frame)
            print("Detected: " + str(int(result['class_id'])))
            if int(result['class_id']) == 30:
                print("ITS BULLSEYE" + str(int(result['class_id'])))
                cap.release()
                cv2.destroyAllWindows()
                return 30;
            elif int(result['class_id']) is not None:
                print(labels[int(result['class_id'])])
                filename="../capturedImage/"+labels[int(result['class_id'])]+".jpg"
                cv2.imwrite(filename, frame)
                print("Saved image for " + labels[int(result['class_id'])])
                cap.release()
                cv2.destroyAllWindows()
                return int(result['class_id']);
        cv2.imshow('Pi Feed', frame)

        if cv2.waitKey(10) & 0xFF ==ord('q'):
            cap.release()
            cv2.destroyAllWindows()

if __name__ == "__main__":
    main()