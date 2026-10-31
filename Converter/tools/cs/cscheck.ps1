# Compiles C# sources with the Roslyn compiler bundled in PowerShell (C# 9, like Unity 2021.3+).
# usage: pwsh cscheck.ps1 -Out out.dll -Dirs dir1,dir2 [-Defines A,B] [-Refs a.dll,b.dll] [-Max 200]
param([string]$Out, [string]$Dirs, [string]$Defines = "", [string]$Refs = "", [int]$Max = 300, [switch]$Warnings, [string]$Lang = "CSharp9")
$dirList = @($Dirs -split ',' | Where-Object { $_ })
$defList = [string[]]@($Defines -split ',' | Where-Object { $_ })
$refList = @($Refs -split ',' | Where-Object { $_ })
$ErrorActionPreference = 'Stop'
if ($PSStyle) { $PSStyle.OutputRendering = 'PlainText' }
Add-Type -Path "$PSHOME/Microsoft.CodeAnalysis.dll"
Add-Type -Path "$PSHOME/Microsoft.CodeAnalysis.CSharp.dll"
$files = foreach ($d in $dirList) { Get-ChildItem -Recurse -File -Filter *.cs -Path $d }
$po = [Microsoft.CodeAnalysis.CSharp.CSharpParseOptions]::new([Microsoft.CodeAnalysis.CSharp.LanguageVersion]$Lang,
      [Microsoft.CodeAnalysis.DocumentationMode]::None, [Microsoft.CodeAnalysis.SourceCodeKind]::Regular, $defList)
$trees = [System.Collections.Generic.List[Microsoft.CodeAnalysis.SyntaxTree]]::new()
foreach ($f in $files) {
  $text = [Microsoft.CodeAnalysis.Text.SourceText]::From([IO.File]::ReadAllText($f.FullName), [Text.Encoding]::UTF8)
  $trees.Add([Microsoft.CodeAnalysis.CSharp.CSharpSyntaxTree]::ParseText($text, $po, $f.FullName, [Threading.CancellationToken]::None))
}
$mrefs = [System.Collections.Generic.List[Microsoft.CodeAnalysis.MetadataReference]]::new()
foreach ($p in ([AppContext]::GetData("TRUSTED_PLATFORM_ASSEMBLIES") -split [IO.Path]::PathSeparator)) {
  if ($p -match '[\\/](System\.[^\\/]*|mscorlib|netstandard)\.dll$') {
    $mrefs.Add([Microsoft.CodeAnalysis.MetadataReference]::CreateFromFile($p))
  }
}
foreach ($r in $refList) { $mrefs.Add([Microsoft.CodeAnalysis.MetadataReference]::CreateFromFile((Resolve-Path $r).Path)) }
$co = [Microsoft.CodeAnalysis.CSharp.CSharpCompilationOptions]::new([Microsoft.CodeAnalysis.OutputKind]::DynamicallyLinkedLibrary)
$co = $co.WithOptimizationLevel([Microsoft.CodeAnalysis.OptimizationLevel]::Release).WithNullableContextOptions([Microsoft.CodeAnalysis.NullableContextOptions]::Disable)
$name = [IO.Path]::GetFileNameWithoutExtension($Out)
$comp = [Microsoft.CodeAnalysis.CSharp.CSharpCompilation]::Create($name, $trees, $mrefs, $co)
$fs = [IO.File]::Create($Out)
try { $res = $comp.Emit($fs) } finally { $fs.Dispose() }
$errs = @($res.Diagnostics | Where-Object { $_.Severity -eq [Microsoft.CodeAnalysis.DiagnosticSeverity]::Error })
$warns = @($res.Diagnostics | Where-Object { $_.Severity -eq [Microsoft.CodeAnalysis.DiagnosticSeverity]::Warning })
Write-Output ("files: {0}  errors: {1}  warnings: {2}" -f $files.Count, $errs.Count, $warns.Count)
$errs | Select-Object -First $Max | ForEach-Object { $_.ToString() }
if ($Warnings) { $warns | Select-Object -First $Max | ForEach-Object { $_.ToString() } }
if ($errs.Count -gt 0) { Remove-Item $Out -ErrorAction SilentlyContinue; exit 1 }
