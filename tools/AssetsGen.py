import json
import os
import sys


from pykrige.ok import OrdinaryKriging
import numpy as np
from PIL import Image

assets_output = "assets_gen"
assets_input = sys.argv[1]

copy_dir_structure = False

# shutil.rmtree(assets_output, ignore_errors=True)
# os.makedirs(assets_output, exist_ok=True)
for root, dirs, files in os.walk(assets_input):
    for file_name in files:
        file_path = os.path.join(root, file_name)
        if file_name.endswith(".json"):
            with open(file_path, 'r') as file:
                config = json.loads(file.read())
                # print(j)
                name = file_name.split(".")[0]
                print(name)
                w = config['width']
                h = config['height']
                canvas = Image.new("RGBA", (w, h), (255, 255, 255, 0))
                for layer in config["layers"]:
                    src = Image.open(os.path.join(root, layer["src"]))
                    src=src.convert("RGBA")
                    texture = Image.open(os.path.join("assets_raw" ,"textures", layer["texture"]))
                    texture=texture.convert("RGBA")
                    for i in range(0, w):
                        for j in range(0, h):
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

                if os.path.exists(os.path.join(root, name + ".normal.png")):
                    height=Image.open(os.path.join(root, name + ".normal.png"))
                    height=height.convert("RGBA")
                    ix=[]
                    iy=[]
                    values=[]
                    for i in range(0, w):
                        for j in range(0, h):
                            r, g, b, a = height.getpixel((i, j))
                            gray=0.299*r+0.587*g+0.114*b
                            if a==0: continue
                            ix.append(i)
                            iy.append(j)
                            values.append(gray)
                    gridx = np.arange(0.0,w,1.0 )  # 三个参数的意思：范围0.0 - 0.6 ，每隔0.1划分一个网格
                    gridy = np.arange(0.0, h, 1.0)
                    ok3d = OrdinaryKriging(ix, iy,values, variogram_model="exponential")  # 模型
                    # variogram_model是变差函数模型，pykrige提供 linear, power, gaussian, spherical, exponential, hole-effect几种variogram_model可供选择，默认的为linear模型。
                    # 使用不同的variogram_model，预测效果是不一样的，应该针对自己的任务选择合适的variogram_model。
                    k3d1, ss3d = ok3d.execute("grid", gridx, gridy)  # k3d1是结果，给出了每个网格点处对应的值

                    # print(np.round(k3d1, 2))
                    # # 输出的结果
                    # # [[0.91 0.87 0.81 0.75 0.7 0.66]
                    # #  [0.92 0.9 0.8 0.74 0.68 0.63]
                    # #  [0.92 0.89 0.81 0.72 0.65 0.59]
                    # #  [0.91 0.9 0.8 0.7 0.62 0.55]
                    # #  [0.88 0.84 0.77 0.68 0.59 0.5]
                    # #  [0.84 0.8 0.74 0.67 0.59 0.53]]
                    #
                    # # 绘图
                    # fig, (ax1) = plt.subplots(1)
                    # ax1.imshow(k3d1, origin="upper")
                    # ax1.set_title("ordinary kriging")
                    # plt.tight_layout()
                    # plt.show()

                    normal_map = Image.new("RGBA", (w, h), (0, 0, 0, 0))
                    for i in range(1, w - 1):
                        for j in range(1, h - 1):
                            r, g, b, a = canvas.getpixel((i, j))
                            if a==0: continue
                            dzdx = (k3d1[j, i + 1] - k3d1[j, i - 1]) / 2.0
                            dzdy = -(k3d1[j + 1, i] - k3d1[j - 1, i]) / 2.0
                            normal = np.array([-dzdx, -dzdy, 1.0])
                            normal = (normal / np.linalg.norm(normal)) * 0.5 + 0.5
                            normal_map.putpixel((i, j), tuple((normal * 255).astype(int)) + (255,))

                    normal_output_path = os.path.join(assets_output, f"{name}.normal.png")
                    normal_map.save(normal_output_path)
                    print(f"Normal map saved to: {normal_output_path}")

                if copy_dir_structure:
                    os.makedirs(os.path.dirname(output_path), exist_ok=True)
                    output_path = os.path.join(
                        "assets_gen", os.path.relpath(root, os.path.join("assets_raw","assets")), f"{name}.png")
                else:
                    output_path = os.path.join(assets_output, f"{name}.png")

                canvas.save(output_path)
                print(f"Directory: {root}")
        print(f"File: {file_name}")

# Example usage
