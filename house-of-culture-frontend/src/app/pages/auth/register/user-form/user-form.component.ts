import {Component, Input, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule} from "@angular/material/core";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {MatSelectModule} from "@angular/material/select";

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, MatInputModule, MatDatepickerModule, MatButtonModule, MatNativeDateModule, ReactiveFormsModule, MatIconModule, MatSelectModule],
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss']
})
export class UserFormComponent implements OnInit {
  @Input() userForm!: FormGroup;

  hide = true;
  ngOnInit(): void {
  }

  dateFilter = (date: Date | null): boolean => {
    const day = (date || new Date());
    const currentDate = new Date();
    return day <= currentDate;
  };

}
