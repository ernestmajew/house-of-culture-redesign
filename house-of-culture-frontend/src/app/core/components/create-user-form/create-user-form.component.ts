import {Component, Input} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {UserRoleTs} from "../../../../../out/api";
import {MatButtonModule} from "@angular/material/button";


export type CreateUserType = "add-person" | "add-child";
@Component({
  selector: 'app-create-user-form',
  standalone: true,
  imports: [CommonModule, MatInputModule, MatDatepickerModule, MatButtonModule, MatNativeDateModule, ReactiveFormsModule, MatIconModule, MatSelectModule],
  templateUrl: './create-user-form.component.html',
  styleUrls: ['./create-user-form.component.scss']
})
export class CreateUserFormComponent {
  @Input() userForm!: FormGroup;
  @Input() type!: CreateUserType;

  hide = true;
  roles: UserRoleTs[] = []

  ngOnInit(): void {
    this.roles = Object.keys(UserRoleTs).map(key => UserRoleTs[key as keyof typeof UserRoleTs])
  }

  dateFilter = (date: Date | null): boolean => {
    const day = (date || new Date());
    const currentDate = new Date();
    return day <= currentDate;
  };

}
