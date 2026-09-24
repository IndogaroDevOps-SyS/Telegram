import os
import sys
import glob

LOG_PATH = "/sdcard/indogaro_dev_log.txt"

def write_log(data):
    with open(LOG_PATH, "a", encoding="utf-8") as f:
        f.write(data + "\n")

def search_code(keyword, root_dir="TMessagesProj"):
    header = f"=== HASIL PENCARIAN KODE: '{keyword}' ===\n"
    print(header)
    write_log(header)
    
    # Mencari semua file secara rekursif tanpa batasan ekstensi (.java, .xml, .gradle, .kt, dll)
    files = [os.path.join(dp, f) for dp, dn, filenames in os.walk(root_dir) for f in filenames]
    found_count = 0
    keyword_lower = keyword.lower()
    
    for file_path in files:
        # Lewati folder build atau bin yang tidak perlu agar pencarian lebih cepat
        if "/build/" in file_path or "/.git/" in file_path:
            continue
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                lines = f.readlines()
            for idx, line in enumerate(lines):
                if keyword_lower in line.lower():
                    found_count += 1
                    res = f"[FOUND] {file_path}:{idx + 1}\n   -> {line.strip()}"
                    print(res)
                    write_log(res + "\n")
        except Exception as e:
            continue
            
    summary = f"=== TOTAL KETEMU: {found_count} baris ===\n"
    print(summary)
    write_log(summary)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Gunakan: python3 tls.py <keyword>")
        sys.exit(1)
    
    with open(LOG_PATH, "w", encoding="utf-8") as f:
        f.write("=== INDOGARO DEV TOOL SESSION LOG ===\n")
        
    keyword_to_find = sys.argv[1]
    search_code(keyword_to_find)
