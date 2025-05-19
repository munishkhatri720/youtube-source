import os
import subprocess
from typing import Tuple, List
import sys

OUTPUT_DIR = './common/src/main/java'

def find_protos(dir: str) -> List[str]:
    proto_files: List[str] = []
    for dir_path, _, filenames in os.walk(dir):
        for fname in filenames:
            if fname.endswith('.proto'):
                proto_files.append(os.path.join(dir_path, fname))
    return proto_files

def gen_proto(protos_path: str, proto_files: List[str]) -> None:
    for file in proto_files:
        cmd = [
            'protoc',
            f'--proto_path={protos_path}',
            f'--java_out={OUTPUT_DIR}',
            file
        ]
        try:
            subprocess.run(cmd, check=True)
            print(f"Compiled {file} → Java classes in {OUTPUT_DIR}")
        except subprocess.CalledProcessError as e:
            print(f"Error compiling {file}: {e}", file=sys.stderr)

if __name__ == "__main__":
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    protos = find_protos(dir="./protos")
    gen_proto(protos_path='./protos', proto_files=protos)
