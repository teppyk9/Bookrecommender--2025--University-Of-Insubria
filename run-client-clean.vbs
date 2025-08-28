Set sh = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
base = fso.GetParentFolderName(WScript.ScriptFullName)

cmd = """" & base & "\run-client.cmd" & """"
sh.Run cmd, 0, False