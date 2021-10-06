import os


def create_collages(image_dir):
    image_paths = os.listdir
    n = len(image_paths)
    # find nearest square
    collage_size = int(math.floor(math.sqrt(len(good_paths))))

    # horizontally stacking images to create rows
    rows = []
    k = 0 # counter for number of rows
    for i in range(collage_size**2):
        if i % collage_size == 0: # finished with row, start new one
            if k > 0:
                rows.append(cur_row)

            cur_row = cv2.imread(os.path.join(image_dir, image_paths[i]))
            k += 1
        else:             # continue stacking images to current row
            cur_img = cv2.imread(os.path.join(image_dir, image_paths[i]))
            cur_row = np.hstack([cur_row, cur_img])

        # vertically stacking rows to create final collage.
        collage = rows[0]

        for i in range(1, len(rows)):
            collage = np.vstack([collage, rows[i]])

    return collage