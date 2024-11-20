import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Editor, Toolbar} from "ngx-editor";

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit, OnDestroy {
  editor!: Editor;
  toolbar: Toolbar = [
    ['bold', 'italic', 'underline'],
    ['blockquote'],
    ['ordered_list', 'bullet_list'],
    ['link'],
    ['text_color', 'background_color'],
    ['align_left', 'align_center', 'align_right', 'align_justify'],
  ];

  editorContent: string = '';
  isTouched: boolean = false;
  @Input() value?: string;
  @Input() placeholder!: string;
  @Input() errorMessage!: string;
  @Output() htmlContentChange = new EventEmitter<string>();

  ngOnInit(): void {
    this.editor = new Editor();
    if (this.value){
      this.editorContent = this.value;
    }
  }

  ngOnDestroy(): void {
    this.editor.destroy();
  }

  onEditorContentChange(): void {
    this.htmlContentChange.emit(this.editorContent);
  }

  touched(): void {
    this.isTouched = true;
  }

  get isError(): boolean {
    // this 7 means length of <p></p> which is the content when you write something and delete
    return this.editorContent.length <= 7 && this.isTouched;
  }
}
