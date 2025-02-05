import json
import os
import shutil
from argparse import RawTextHelpFormatter
from importlib.resources import read_text

from PIL import Image

assets_output = "assets_gen"
assets_input = "assets_raw/assets"
copy_dir_structure = False

shutil.rmtree(assets_output, ignore_errors=True)
os.makedirs(assets_output, exist_ok=True)
for root, dirs, files in os.walk(assets_input):
    for file_name in files:
        file_path = os.path.join(root, file_name)
        if file_name.endswith(".json"):
            with open(file_path, 'r') as file:
                config = json.loads(file.read())
                # print(j)
                name = config["name"]
                print(name)
                canvas = Image.new("RGBA", (config['width'], config['height']), (255, 255, 255, 0))
                for layer in config["layers"]:
                    src = Image.open(os.path.join(root, layer["src"]))
                    src=src.convert("RGBA")
                    texture = Image.open(os.path.join("assets_raw/textures", layer["texture"]))
                    texture=texture.convert("RGBA")
                    for i in range(0, config['width']):
                        for j in range(0, config['height']):
                            r, g, b, a = src.getpixel((i, j))
                            alpha = a / 255.0
                            r1, g1, b1, a1 = texture.getpixel((i, j))
                            if r == 0 and g == 255 and b == 0:
                                continue
                            canvas.putpixel((i, j),
                                            (int(r * alpha + r1 * (1 - alpha)), int(g * alpha + g1 * (1 - alpha)),
                                             int(b * alpha + b1 * (1 - alpha)), a1))
                            # (int(r * rate + r1 * (1 - rate)), int(g * rate + g1 * (1 - rate)),
                            #  int(b * rate + b1 * (1 - rate)), int(a * rate + a1 * (1 - rate)))
                            # )

                if copy_dir_structure:
                    os.makedirs(os.path.dirname(output_path), exist_ok=True)
                    output_path = os.path.join(
                        "assets_gen", os.path.relpath(root, "assets_raw/assets"), f"{name}.png")
                else:
                    output_path = os.path.join(assets_output, f"{name}.png")

                canvas.save(output_path)
                print(f"Directory: {root}")
        print(f"File: {file_name}")

# Example usage
