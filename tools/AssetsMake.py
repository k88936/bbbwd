import os

dir_structure = ["assets_raw", "assets", ".dep"]


def generate_makefile(base_dir):
    with open(os.path.join(dir_structure[0], "Makefile"), 'w') as makefile:
        targets = []
        makefile.write(".DEFAULT_GOAL := all\n")
        for subdir, _, files in os.walk(base_dir):
            if subdir == base_dir:
                continue
            target = os.path.basename(subdir)
            dependencies = " ".join(
                [os.path.join(dir_structure[1], target, f) for f in files if not f == dir_structure[2]])
            dep_file = os.path.join(dir_structure[1], target, dir_structure[2])
            makefile.write(f"{dep_file}: {dependencies}\n"
                           f"\t@echo {dependencies} > {dep_file}\n"
                           f"\tcd ../&& python {os.path.join("tools", "AssetsGen.py")} {os.path.join(base_dir, target)}\n"
                           f"{target}: {dep_file}\n"
                           )
            targets.append(target)
        makefile.write(f"all: {' '.join(targets)}\n"
                       f"default: all\n"
                       f"clean:\n"
                       f"\t rm {os.path.join(dir_structure[1], "*", dir_structure[2])}\n")


directory = os.path.join(dir_structure[0], dir_structure[1])
generate_makefile(directory)
