Set sh = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
base = fso.GetParentFolderName(WScript.ScriptFullName)

cmd = """" & base & "\run-server.cmd" & """"
sh.Run cmd, 0, False
