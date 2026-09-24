import os
import sys
import argparse

def search_code():
    parser = argparse.ArgumentParser(description="Indogaro Code Search Tool (IL)")
    parser.add_argument("-n", "--name", required=True, help="Keyword atau teks yang dicari")
    parser.add_argument("-l", "--log", default="/sdcard/il_search_result.txt", help="Path file output log")
    parser.add_argument("-p", "--path", default="TMessagesProj", help="Direktori atau spesifik file target pencarian")
    
    args = parser.parse_args()
    
    target = args.path
    keyword = args.name
    log_file = args.log
    
    found_results = []
    
    if os.path.isfile(target):
        files_to_search = [target]
    else:
        files_to_search = []
        for root, dirs, files in os.walk(target):
            for file in files:
                if file.endswith((".java", ".kt", ".py", ".xml", ".gradle")):
                    files_to_search.append(os.path.join(root, file))
                    
    print(f"=== PENCARIAN KODE: \x1b[36m{keyword}\x1b[0m (Target: {target}) ===")
    
    for filepath in files_to_search:
        try:
            with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
                for line_num, line in enumerate(f, 1):
                    if keyword in line:
                        res = f"[FOUND] {filepath}:{line_num}\n   -> {line.strip()}\n"
                        found_results.append(res)
        except Exception as e:
            pass
            
    output = f"=== INDOGARO DEV TOOL LOG: '{keyword}' ===\n"
    if found_results:
        output += "".join(found_results)
        output += f"=== TOTAL KETEMU: {len(found_results)} baris ==="
    else:
        output += "=== TIDAK ADA HASIL YANG DITEMUKAN ==="
        
    with open(log_file, "w", encoding="utf-8") as f:
        f.write(output)
        
    print(output)
    print(f"\nLog disimpan ke: {log_file}")

if __name__ == "__main__":
    search_code()
