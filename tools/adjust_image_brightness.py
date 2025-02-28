from PIL import Image
import sys
import os

def adjust_image_brightness(image_path):
    # Open the image file
    with Image.open(image_path) as img:
        # Convert the image to RGB mode if it's not already
        img = img.convert("RGBA")
        # Get the pixel data
        pixels = img.load()

        # Iterate over each pixel
        for i in range(img.width):
            for j in range(img.height):
                r, g, b,a = pixels[i, j]
                # Reduce each RGB value to 0.5 times its original value
                # pixels[i, j] = (int(r * 0.5), int(g * 0.5), int(b * 0.5),a)
                factor = a/255.0 *0.5
                pixels[i, j] = (int(r * factor), int(g *factor), int(b * factor),255)
                # pixels[i, j] = (r, g, b, int(a * 0.5))

        # Split the directory and file name
        directory, file_name = os.path.split(image_path)
        # Create the new file name with the "adjusted_" prefix
        new_file_name = "adjusted_" + file_name
        # Join the directory and new file name to get the output path
        output_path = os.path.join(directory, new_file_name)

        # Create the output directory if it doesn't exist
        os.makedirs(directory, exist_ok=True)

        # Save the modified image
        img.save(output_path)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python adjust_brightness.py <image_path>")
    else:
        adjust_image_brightness(sys.argv[1])