import os
import sys
import argparse

def write_log(log_path, data):
    with open(log_path, "a", encoding="utf-8") as f:
        f.write(data + "\n")

def search_code(keyword, log_filename, root_dir="TMessagesProj"):
    # Tentukan jalur penyimpanan log di /sdcard/ dengan nama kustom
    log_path = f"/sdcard/{log_filename}" if not log_filename.startswith("/") else log_filename
    
    # Inisialisasi file log baru
    with open(log_path, "w", encoding="utf-8") as f:
        f.write(f"=== INDOGARO DEV TOOL LOG: '{keyword}' ===\n")

    header = f"=== PENCARIAN KODE: '{keyword}' (Target: {root_dir}) ===\n"
    print(header)
    write_log(log_path, header)
    
    files = [os.path.join(dp, f) for dp, dn, filenames in os.walk(root_dir) for f in filenames]
    found_count = 0
    keyword_lower = keyword.lower()
    
    for file_path in files:
        # Abaikan folder build, git, atau cache agar pencarian fokus
        if "/build/" in file_path or "/.git/" in file_path or "/bin/" in file_path:
            continue
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                lines = f.readlines()
            for idx, line in enumerate(lines):
                if keyword_lower in line.lower():
                    found_count += 1
                    res = f"[FOUND] {file_path}:{idx + 1}\n   -> {line.strip()}"
                    print(res)
                    write_log(log_path, res + "\n")
        except Exception as e:
            continue
            
    summary = f"=== TOTAL KETEMU: {found_count} baris ===\nLog disimpan ke: {log_path}\n"
    print(summary)
    write_log(log_path, summary)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Indogaro Advanced CLI Dev Tool untuk Pencarian Kode & Log Otomatis.")
    parser.add_argument("-n", "--name", required=True, help="Kata kunci atau string kode yang ingin dicari.")
    parser.add_argument("-l", "--log", default="indogaro_dev_log.txt", help="Nama file output log di /sdcard/ (default: indogaro_dev_log.txt).")
    parser.add_argument("-d", "--dir", default="TMessagesProj", help="Direktori root yang ingin dipindai (default: TMessagesProj).")
    
    args = parser.parse_args()
    search_code(args.name, args.log, args.dir)
