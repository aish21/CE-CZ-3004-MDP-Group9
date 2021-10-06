from PIL import Image, ImageEnhance
import os
import math

def stitch():
    path = os.getcwd()
    parent = os.path.join(path, os.pardir)
    image_path_url = os.path.abspath(parent) + "/capturedImage"

    image_paths = os.listdir(image_path_url)
    print(image_paths)
    print(len(image_paths))

    # find nearest square
    collage_size = int(math.ceil(math.sqrt(len(image_paths))))
    size = 0
    #Dynamically setting collage length and width by square
    for i in range(collage_size):
        size += 500
    collage = Image.new("RGBA", (size, size))

    i = 0
    x = 0
    y = 0
    while i < len(image_paths):
        for j in range(collage_size):
            img = Image.open(os.path.join(image_path_url, image_paths[i]))
            print(image_paths[i] + " at " + str(x) + ", " + str(y))
            img = img.resize((500,500))
            collage.paste(img, (x, y))
            i += 1
            #End collage if no more images
            if i >= len(image_paths):
                break
            x += 500
            #Finished row, resetting x to 0
            if x == size:
                x = 0
                break
        y += 500

    collage.show()
    collage.save(os.path.abspath(parent) + "/collageImg.png")
if __name__ == "__main__":
    stitch()