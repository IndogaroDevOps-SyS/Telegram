import os
import sys
import glob

LOG_PATH = "/sdcard/indogaro_dev_log.txt"

def write_log(data):
    with open(LOG_PATH, "a", encoding="utf-8") as f:
        f.write(data + "\n")
    print(f"[*] Log tersimpan ke {LOG_PATH}")

def search_code(keyword, root_dir="TMessagesProj/src/main/java/id/indogaro"):
    output = f"=== HASIL PENCARIAN KODE: '{keyword}' ===\n"
    print(output)
    write_log(output)
    
    files = glob.glob(f"{root_dir}/**/*.java", recursive=True)
    found_count = 0
    
    for file_path in files:
        try:
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                lines = f.readlines()
            for idx, line in enumerate(lines):
                if keyword in line:
                    found_count += 1
                    res = f"[FOUND] {file_path}:{idx + 1}\n   -> {line.strip()}"
                    print(res)
                    write_log(res)
        except Exception as e:
            continue
            
    summary = f"=== TOTAL KETEMU: {found_count} baris ===\n"
    print(summary)
    write_log(summary)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Gunakan: python3 indogaro_tool.py <keyword>")
        sys.exit(1)
    
    # Reset log file setiap kali tool dijalankan baru
    with open(LOG_PATH, "w", encoding="utf-8") as f:
        f.write("=== INDOGARO DEV TOOL SESSION LOG ===\n")
        
    keyword_to_find = sys.argv[1]
    search_code(keyword_to_find)
