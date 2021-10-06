from PIL import Image, ImageEnhance
import os
import math
from pathlib import Path

path = os.getcwd()
parent = os.path.join(path, os.pardir)
image_path = os.path.abspath(parent) + "/capturedImage"
print(image_path)